package ir.siamak.fintrack.personalaccountant.banksms

import androidx.room.withTransaction
import ir.siamak.fintrack.di.PersonalAccountingDatabase
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionEntity
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import java.math.BigDecimal
import ir.siamak.fintrack.personalaccountant.data.local.entity.WalletEntity
import javax.inject.Inject

/** The only path from a reviewed SMS into the ledger. No receiver can call this implicitly. */
class ImportBankSms @Inject constructor(@PersonalAccountingDatabase private val db: AppDatabase, private val inbox: BankSmsInbox) {
    suspend operator fun invoke(candidate: BankSmsCandidate, amount: BigDecimal, kind: BankSmsKind, walletId: Long, toWalletId: Long?, memberId: Long, reportedBalance: BigDecimal? = null, newWalletName: String? = null, balanceOnTarget: Boolean = candidate.parsed.incoming == true) {
        val marker = "[FinTrackSms:${candidate.id}]"
        db.withTransaction {
            if(db.transactionDao().hasSmsMarker(marker)) return@withTransaction
            require(amount.signum() > 0) { "مبلغ باید مثبت باشد" }
            val value = amount.toDouble()
            require(value.isFinite() && BigDecimal.valueOf(value).compareTo(amount) == 0) { "مبلغ خارج از دقت مجاز است" }
            val member = requireNotNull(db.memberDao().getMemberById(memberId)) { "شخص پیدا نشد" }
            require(!member.isDeleted && member.contextId == 1L) { "شخص معتبر نیست" }
            require(kind != BankSmsKind.TRANSFER || walletId != 0L || toWalletId != 0L) { "یکی از حساب‌های انتقال باید از قبل مشخص باشد" }
            suspend fun resolve(id: Long): Long = if(id == 0L) {
                val matches = db.walletDao().smsBankWallets().filter { !it.isCash && it.contextId == 1L &&
                    BankSmsAccounts.matches(candidate.parsed,it.name,it.cardNumber,it.accountNumber) }
                require(matches.size <= 1) { "چند حساب مشابه وجود دارد؛ حساب را انتخاب کنید" }
                matches.singleOrNull()?.id ?: run {
                    val name = newWalletName?.trim().orEmpty()
                    require(name.isNotBlank()) { "نام حساب جدید را وارد کنید" }
                    require(db.walletDao().smsBankWallets().none { it.contextId == 1L && it.name == name }) { "حسابی با این نام وجود دارد؛ آن را انتخاب کنید" }
                    val number = candidate.parsed.accountHints.singleOrNull()?.takeIf { it.length >= 10 }
                    db.walletDao().insertWallet(WalletEntity(name = name,balance = 0.0,color = "#1565C0",cardNumber = number?.takeIf { it.length == 16 && candidate.body.contains("کارت") },
                        accountNumber = number?.takeUnless { it.length == 16 && candidate.body.contains("کارت") }))
                }
            } else id
            val wallet = requireNotNull(db.walletDao().getWalletById(resolve(walletId))) { "حساب پیدا نشد" }
            require(!wallet.isDeleted && !member.isDeleted && wallet.contextId == 1L && member.contextId == wallet.contextId) { "حساب و شخص باید فعال و متعلق به حسابدار شخصی باشند" }
            val target = if(kind == BankSmsKind.TRANSFER) {
                require(wallet.id != toWalletId) { "حساب مبدأ و مقصد باید متفاوت باشند" }
                requireNotNull(toWalletId?.let { db.walletDao().getWalletById(resolve(it)) }) { "حساب مقصد را انتخاب کنید" }.also {
                    require(it.id != wallet.id && !it.isDeleted && it.contextId == wallet.contextId) { "حساب مقصد معتبر نیست" }
                }
            } else null
            fun balance(old: Double, delta: BigDecimal): Double {
                val exact = BigDecimal.valueOf(old).add(delta); val number = exact.toDouble()
                require(number.isFinite() && BigDecimal.valueOf(number).compareTo(exact) == 0) { "موجودی خارج از دقت مجاز است" }
                return number
            }
            val delta = if(kind == BankSmsKind.INCOME) amount else amount.negate()
            require(reportedBalance == null || reportedBalance.signum() >= 0) { "مانده نامعتبر است" }
            val snapshotId = if(reportedBalance == null) null else if(kind == BankSmsKind.TRANSFER && balanceOnTarget) target?.id else wallet.id
            suspend fun resulting(account: WalletEntity, change: BigDecimal): Double {
                val exact = BankSmsBalances.resulting(account.id,BigDecimal.valueOf(account.balance),change,
                    candidate.receivedAt,if(account.id == snapshotId) reportedBalance else null,db.transactionDao().smsWalletHistory(account.id))
                return balance(0.0,exact)
            }
            val sourceBalance = resulting(wallet,delta)
            val targetBalance = target?.let { resulting(it,amount) }
            val type = when(kind) { BankSmsKind.INCOME -> TransactionType.INCOME; BankSmsKind.EXPENSE -> TransactionType.EXPENSE; BankSmsKind.TRANSFER -> TransactionType.TRANSFER }
            db.transactionDao().insertTransaction(TransactionEntity(amount = value, type = type, categoryName = "پیامک بانکی تأییدشده",
                walletId = wallet.id, toWalletId = target?.id, memberId = member.id, date = candidate.receivedAt,
                note = marker + "\n" + (snapshotId?.let { "[FinTrackSmsBalance:$it]" } ?: "[FinTrackSmsTransaction]") + "\n" + candidate.body, contextId = wallet.contextId))
            db.walletDao().updateWallet(wallet.copy(balance = sourceBalance, updatedAt = System.currentTimeMillis()))
            if(target != null) db.walletDao().updateWallet(target.copy(balance = requireNotNull(targetBalance), updatedAt = System.currentTimeMillis()))
        }
        // A crash here is harmless: retry sees the committed marker, then clears the pending item.
        inbox.reviewed(candidate.id)
    }
}

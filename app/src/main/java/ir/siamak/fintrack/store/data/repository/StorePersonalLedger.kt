package ir.siamak.fintrack.store.data.repository

import androidx.room.withTransaction
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionEntity
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton
import java.math.BigDecimal

/** Explicit opt-in bridge. Personal transaction + wallet balance commit atomically in AppDatabase.
 * StoreDatabase is the durable source/outbox: failed posting remains visible and can be retried.
 * Replaying a cumulative receipt never credits the same amount twice. */
@Singleton class StorePersonalLedger @Inject constructor(@ir.siamak.fintrack.di.PersonalAccountingDatabase private val db: AppDatabase) {
    val directory = combine(db.walletDao().getAllWallets(), db.memberDao().getAllMembers(), db.transactionDao().getAllTransactions()) { wallets, members, rows ->
        val receiptGroups = rows.filter { !it.isDeleted && it.note.startsWith("[FinTrackStore:") }.groupBy { it.note.substringAfter("[FinTrackStore:").substringBefore(']') }
        val amounts = receiptGroups.mapValues { (_, all) -> runCatching {
            all.fold(0L) { a,b -> require(b.type == TransactionType.INCOME && b.amount >= 0); Math.addExact(a,BigDecimal.valueOf(b.amount).longValueExact()) }
        } }
        StoreFinanceDirectory(wallets.filter { !it.isDeleted && it.contextId == ir.siamak.fintrack.personalaccountant.data.local.entity.AccountantContextIds.PERSONAL }.map { FinanceChoice(it.id,it.contextId,it.name) },
            members.filterNot { it.isDeleted }.map { FinanceChoice(it.id,it.contextId,it.name) },
            amounts.filterValues { it.isSuccess }.mapValues { it.value.getOrThrow() }, amounts.filterValues { it.isFailure }.keys)
    }
    suspend fun validate(account: StoreSettlementAccount) = db.withTransaction {
        val wallet = requireNotNull(db.walletDao().getWalletById(account.walletId)) { "کیف پول مقصد پیدا نشد" }
        val member = requireNotNull(db.memberDao().getMemberById(account.memberId)) { "شخص دریافت‌کننده پیدا نشد" }
        require(!wallet.isDeleted && !member.isDeleted && wallet.contextId == account.contextId && member.contextId == account.contextId) { "کیف پول و شخص باید متعلق به یک حساب فعال باشند" }
        require(account.contextId == ir.siamak.fintrack.personalaccountant.data.local.entity.AccountantContextIds.PERSONAL) { "مقصد باید حساب شخصی باشد" }
        // v8 fresh installs do not execute MIGRATION_7_8's registry seed. Create only the missing
        // fixed local context; never replace/reactivate an existing row or choose a different context.
        db.openHelper.writableDatabase.execSQL("INSERT OR IGNORE INTO accountant_contexts (id,ownerUserId,type,name,description,isActive,createdAt,updatedAt) VALUES (1,0,'PERSONAL','Personal',NULL,1,0,0)")
        db.openHelper.readableDatabase.query("SELECT id FROM accountant_contexts WHERE id = ? AND ownerUserId = 0 AND isActive = 1 AND type = 'PERSONAL'", arrayOf(account.contextId)).use {
            require(it.moveToFirst()) { "حساب شخصی مقصد فعال نیست" }
        }
    }
    suspend fun post(invoice: Invoice): Long {
        val account = invoice.settlement ?: return 0L
        val received = invoice.payment?.paidAmount ?: 0L
        if(received == 0L) return 0L
        return db.withTransaction {
            validate(account)
            val marker = "[FinTrackStore:${invoice.invoiceNumber}]"
            val previous = db.transactionDao().storeReceipts(account.contextId, marker)
            require(previous.all { it.walletId == account.walletId && it.memberId == account.memberId && it.type == TransactionType.INCOME }) { "سابقه اتصال کیف پول تغییر کرده؛ برای جلوگیری از دوباره‌شماری بررسی لازم است" }
            val posted = previous.fold(0L) { sum,row -> Math.addExact(sum,BigDecimal.valueOf(row.amount).longValueExact()) }
            require(posted >= invoice.walletPostedAmount) { "سابقهٔ دریافت متصل در کیف پول حذف یا کم شده است؛ ثبت خودکار برای جلوگیری از دوباره‌شماری متوقف شد" }
            require(received >= posted) { "مبلغ ثبت‌شده در کیف پول از دریافت فاکتور بیشتر است" }
            val delta = received-posted
            if(delta == 0L) return@withTransaction received
            val wallet = requireNotNull(db.walletDao().getWalletById(account.walletId))
            val amount = delta.toDouble()
            require(BigDecimal.valueOf(amount).compareTo(BigDecimal.valueOf(delta)) == 0) { "مبلغ برای مدل عددی کیف پول بیش از حد بزرگ است" }
            val exactBalance = BigDecimal.valueOf(wallet.balance).add(BigDecimal.valueOf(delta))
            val balance = exactBalance.toDouble()
            require(balance.isFinite() && BigDecimal.valueOf(balance).compareTo(exactBalance) == 0) { "موجودی کیف پول خارج از دقت مجاز است" }
            db.transactionDao().insertTransaction(TransactionEntity(amount = amount, type = TransactionType.INCOME,
                categoryName = "درآمد فروشگاه", walletId = account.walletId, toWalletId = null, memberId = account.memberId,
                date = System.currentTimeMillis(), note = marker + "\n" + invoice.store.name + " · " + InvoiceTrackingCode.of(invoice), contextId = account.contextId))
            db.walletDao().updateWallet(wallet.copy(balance = balance, updatedAt = System.currentTimeMillis()))
            received
        }
    }
}

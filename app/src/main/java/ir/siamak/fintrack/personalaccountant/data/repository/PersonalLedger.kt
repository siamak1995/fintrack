package ir.siamak.fintrack.personalaccountant.data.repository

import androidx.room.withTransaction
import ir.siamak.fintrack.di.PersonalAccountingDatabase
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.personalaccountant.data.local.entity.*
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.domain.analytics.isLoanTag
import java.math.BigDecimal
import javax.inject.Inject

/** A transaction, its tags, balances and linked repayment must all commit or all roll back. */
class PersonalLedger @Inject constructor(@PersonalAccountingDatabase private val db: AppDatabase) {
    suspend fun save(row: Transaction): Long = db.withTransaction {
        val old = if(row.id == 0L) null else requireNotNull(db.transactionDao().getTransactionById(row.id)?.transaction) { "تراکنش پیدا نشد" }
        require(row.amount.isFinite() && row.amount > 0 && row.amount <= 9007199254740991.0) { "مبلغ نامعتبر است" }
        require(row.contextId == 1L && (old == null || old.contextId == row.contextId)) { "حساب مالی نامعتبر است" }
        val member = requireNotNull(db.memberDao().getMemberById(row.memberId)) { "شخص را انتخاب کنید" }
        require(!member.isDeleted && member.contextId == row.contextId) { "شخص معتبر نیست" }
        require(row.type != TransactionType.TRANSFER || (row.toWalletId != null && row.toWalletId != row.walletId)) { "حساب مبدأ و مقصد باید متفاوت باشند" }
        val next = old?.copy(amount = row.amount,type = row.type,categoryName = row.categoryName,walletId = row.walletId,
            toWalletId = row.toWalletId,memberId = row.memberId,date = row.date,note = row.note,loanId = row.loanId,
            updatedAt = System.currentTimeMillis()) ?: row.toEntity()
        if(old != null && protectedFinancialRecord(old)) {
            require(old.amount == next.amount && old.type == next.type && old.walletId == next.walletId && old.toWalletId == next.toWalletId &&
                old.date == next.date && old.note == next.note && old.memberId == next.memberId) { "مبلغ و اطلاعات دریافت فروشگاه یا مانده تطبیق‌یافته را از سند اصلی تغییر دهید؛ تگ و وام قابل انتخاب‌اند" }
        }
        val tags = row.tags.map { tag -> requireNotNull(db.tagDao().getTagById(tag.id)) { "تگ پیدا نشد" }.also {
            require(!it.isDeleted && it.contextId == row.contextId && it.allowedType == row.type) { "تگ با نوع تراکنش سازگار نیست" }
        } }
        require(row.type == TransactionType.TRANSFER || tags.none { isLoanTag(it.name) } || row.loanId != null) { "برای تگ وام یا قسط، وام مربوط را انتخاب کنید" }
        applyLoan(old,next)
        val deltas = mutableMapOf<Long,BigDecimal>()
        suspend fun collect(record: TransactionEntity, direction: Int) { effects(record).forEach { (id,value) ->
            deltas[id] = (deltas[id] ?: BigDecimal.ZERO).add(value.multiply(BigDecimal(direction)))
        } }
        if(old != null) collect(old,-1)
        collect(next,1)
        applyBalances(deltas,next.contextId,setOfNotNull(next.walletId,next.toWalletId))
        val id = if(old == null) db.transactionDao().insertTransaction(next) else { db.transactionDao().updateTransaction(next); old.id }
        db.transactionDao().deleteTagsForTransaction(id)
        db.transactionDao().insertTransactionTagCrossRefs(tags.map { TransactionTagCrossRef(id,it.id) })
        id
    }

    suspend fun delete(id: Long) = db.withTransaction {
        val row = requireNotNull(db.transactionDao().getTransactionById(id)?.transaction) { "تراکنش پیدا نشد" }
        require(!protectedFinancialRecord(row)) { "برای حفظ تطبیق موجودی، سند فروشگاه یا پیامک دارای مانده را از مسیر اصلی اصلاح کنید" }
        applyLoan(row,null)
        applyBalances(effects(row).mapValues { it.value.negate() },row.contextId,emptySet())
        db.transactionDao().updateTransaction(row.copy(isDeleted = true,updatedAt = System.currentTimeMillis()))
    }

    /** Also used inside SMS approval's surrounding Room transaction. */
    suspend fun applyLoan(old: TransactionEntity?, next: TransactionEntity?) {
        require(next?.loanId == null || next.type != TransactionType.TRANSFER) { "انتقال بین حساب‌های خودتان پرداخت قسط نیست" }
        val deltas = mutableMapOf<Long,BigDecimal>()
        old?.loanId?.let { if(old.type == TransactionType.EXPENSE) deltas[it] = BigDecimal.valueOf(old.amount).negate() }
        next?.loanId?.let { id ->
            val loan = requireNotNull(db.installmentDao().getInstallmentById(id)) { "وام انتخاب‌شده پیدا نشد" }
            require(!loan.isDeleted && loan.contextId == next.contextId) { "وام انتخاب‌شده معتبر نیست" }
            if(next.type == TransactionType.EXPENSE) deltas[id] = (deltas[id] ?: BigDecimal.ZERO).add(BigDecimal.valueOf(next.amount))
        }
        deltas.forEach { (id,delta) ->
            val loan = requireNotNull(db.installmentDao().getInstallmentById(id)) { "وام پیدا نشد" }
            val paid = BigDecimal.valueOf(loan.paidAmount).add(delta)
            require(paid >= BigDecimal.valueOf(loan.openingPaidAmount) && paid <= BigDecimal.valueOf(loan.totalAmount)) { "مبلغ پرداخت قسط از مانده وام بیشتر است یا با پرداخت‌های قبلی سازگار نیست" }
            db.installmentDao().updateInstallment(loan.copy(paidAmount = exact(paid),isPaid = paid >= BigDecimal.valueOf(loan.totalAmount),updatedAt = System.currentTimeMillis()))
        }
    }

    private suspend fun applyBalances(deltas: Map<Long,BigDecimal>, contextId: Long, activeIds: Set<Long>) {
        deltas.forEach { (id,delta) ->
            val wallet = requireNotNull(db.walletDao().getWalletById(id)) { "حساب پیدا نشد" }
            require(wallet.contextId == contextId && (id !in activeIds || !wallet.isDeleted)) { "حساب معتبر نیست" }
            val balance = BigDecimal.valueOf(wallet.balance).add(delta)
            require(delta.signum() >= 0 || balance.signum() >= 0) { "موجودی حساب کافی نیست" }
            if(delta.signum() != 0) db.walletDao().updateWallet(wallet.copy(balance = exact(balance),updatedAt = System.currentTimeMillis()))
        }
    }
    private suspend fun effects(row: TransactionEntity): Map<Long,BigDecimal> {
        val amount = BigDecimal.valueOf(row.amount)
        val raw = when(row.type) {
            TransactionType.INCOME -> mapOf(row.walletId to amount)
            TransactionType.EXPENSE -> mapOf(row.walletId to amount.negate())
            TransactionType.TRANSFER -> mapOf(row.walletId to amount.negate(),requireNotNull(row.toWalletId) to amount)
        }
        return raw.mapValues { (walletId,delta) ->
            val marker = "[FinTrackSmsBalance:$walletId]"
            val covered = db.transactionDao().smsWalletHistory(walletId).any {
                !it.isDeleted && it.id != row.id && it.date > row.date && it.note.lineSequence().drop(1).firstOrNull() == marker
            }
            if(covered) BigDecimal.ZERO else delta
        }
    }
    private fun exact(value: BigDecimal): Double = value.toDouble().also {
        require(it.isFinite() && BigDecimal.valueOf(it).compareTo(value) == 0) { "مبلغ خارج از دقت مجاز است" }
    }
    private fun protectedFinancialRecord(row: TransactionEntity) = row.note.startsWith("[FinTrackStore:") ||
        (row.note.startsWith("[FinTrackSms:") && row.note.lineSequence().drop(1).firstOrNull()?.startsWith("[FinTrackSmsBalance:") == true)
}

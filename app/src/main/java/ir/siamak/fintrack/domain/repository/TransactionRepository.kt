package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * قرارداد دسترسی به داده‌های تراکنش.
 */
interface TransactionRepository {

    /**
     * همه تراکنش‌ها را به صورت جریان برمی‌گرداند.
     */
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * تراکنش‌های مربوط به یک حساب را برمی‌گرداند.
     */
    fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>>

    /**
     * یک تراکنش را بر اساس شناسه پیدا می‌کند.
     */
    suspend fun getTransactionById(id: Long): Transaction?

    /**
     * یک تراکنش جدید ذخیره می‌کند و شناسه آن را برمی‌گرداند.
     */
    suspend fun insertTransaction(transaction: Transaction): Long

    /**
     * یک تراکنش موجود را ویرایش می‌کند.
     */
    suspend fun updateTransaction(transaction: Transaction)

    /**
     * یک تراکنش را حذف می‌کند.
     */
    suspend fun deleteTransaction(transaction: Transaction)

    /**
     * نسخه ساده ذخیره برای بخش‌هایی که فقط ثبت لازم دارند.
     */
    suspend fun insert(transaction: Transaction)

    /**
     * تراکنش‌ها را با فیلتر بازه زمانی برمی‌گرداند.
     */
    suspend fun getTransactionsByDateRange(fromDate: Long?, toDate: Long?): List<Transaction>

    /**
     * تراکنش‌های بازه زمانی را به صورت مستقیم و همگام برمی‌گرداند.
     */
    suspend fun getTransactionsByDateRangeSync(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction>

    /**
     * همه تراکنش‌ها را به صورت لیست همگام برمی‌گرداند.
     */
    suspend fun getAllTransactionsSync(): List<Transaction>
}

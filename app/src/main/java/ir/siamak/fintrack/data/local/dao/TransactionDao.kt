package ir.siamak.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ir.siamak.fintrack.data.local.entity.TagEntity
import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.local.entity.TransactionTagCrossRef
import ir.siamak.fintrack.data.local.entity.TransactionWithTags
import kotlinx.coroutines.flow.Flow

/**
 * دسترسی به داده‌های مربوط به تراکنش‌ها در Room.
 */
@Dao
interface TransactionDao {

    /**
     * یک رابطه بین تراکنش و تگ ذخیره می‌کند.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionTagCrossRef(crossRef: TransactionTagCrossRef)

    /**
     * چند رابطه بین تراکنش و تگ را ذخیره می‌کند.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionTagCrossRefs(crossRefs: List<TransactionTagCrossRef>)

    /**
     * همه تگ‌های یک تراکنش را حذف می‌کند.
     */
    @Query("DELETE FROM transaction_tags WHERE transactionId = :transactionId")
    suspend fun deleteTagsForTransaction(transactionId: Long)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    /**
     * تگ‌های مربوط به یک تراکنش را برمی‌گرداند.
     */
    @Query(
        """
        SELECT tags.* FROM tags
        INNER JOIN transaction_tags ON tags.id = transaction_tags.tagId
        WHERE transaction_tags.transactionId = :transactionId
        ORDER BY tags.name ASC
        """
    )
    fun getTagsForTransaction(transactionId: Long): Flow<List<TagEntity>>

    /**
     * یک تراکنش جدید درج می‌کند.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    /**
     * یک تراکنش را ویرایش می‌کند.
     */
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    /**
     * یک تراکنش را حذف می‌کند.
     */
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    /**
     * همه تراکنش‌ها را به صورت جریان برمی‌گرداند.
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    /**
     * تراکنش‌های یک حساب را به صورت جریان برمی‌گرداند.
     */
    @Query("SELECT * FROM transactions WHERE walletId = :walletId ORDER BY date DESC")
    fun getTransactionsByWallet(walletId: Long): Flow<List<TransactionEntity>>

    /**
     * یک تراکنش را بر اساس شناسه به صورت suspend برمی‌گرداند.
     */
    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Long): TransactionWithTags?

    /**
     * اگر جایی جریان تک‌تراکنش لازم باشد این متد قابل استفاده است.
     */
    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    fun observeTransactionById(transactionId: Long): Flow<TransactionEntity?>

    /**
     * همه تراکنش‌ها را به صورت لیست همگام برمی‌گرداند.
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>

    /**
     * تراکنش‌های بین دو زمان را به صورت همگام برمی‌گرداند.
     */
    @Query(
        """
        SELECT * FROM transactions
        WHERE date >= :startTimestamp AND date <= :endTimestamp
        ORDER BY date DESC
        """
    )
    suspend fun getTransactionsByDateRangeSync(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<TransactionEntity>

    /**
     * آخرین تراکنش‌ها را همراه با تگ‌ها برمی‌گرداند.
     */
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactionsWithTags(limit: Int): Flow<List<TransactionWithTags>>


    /**
     * Returns all transactions with their resolved tags ordered by newest date first.
     *
     * This is the source used by transaction list filtering because tag-based filtering
     * requires full transaction-tag relations to be loaded.
     */
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsWithTags(): Flow<List<TransactionWithTags>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE walletId = :walletId ORDER BY date DESC")
    fun getTransactionsByWalletWithTags(walletId: Long): Flow<List<TransactionWithTags>>


    @Transaction
    @Query(
        """
    SELECT * FROM transactions
    WHERE date >= :startTimestamp AND date <= :endTimestamp
    ORDER BY date DESC
    """
    )
    suspend fun getTransactionsWithTagsByDateRangeSync(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<TransactionWithTags>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsWithTagsSync(): List<TransactionWithTags>

}

package ir.siamak.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import ir.siamak.fintrack.data.local.entity.TagEntity
import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.local.entity.TransactionTagCrossRef
import ir.siamak.fintrack.data.local.entity.TransactionWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionTagCrossRef(crossRef: TransactionTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionTagCrossRefs(crossRefs: List<TransactionTagCrossRef>)

    @Query("DELETE FROM transaction_tags WHERE transactionId = :transactionId")
    suspend fun deleteTagsForTransaction(transactionId: Long)

    @Query("""
    SELECT tags.* FROM tags
    INNER JOIN transaction_tags ON tags.id = transaction_tags.tagId
    WHERE transaction_tags.transactionId = :transactionId
    ORDER BY tags.name ASC
""")
    fun getTagsForTransaction(transactionId: Long): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE walletId = :walletId ORDER BY date DESC")
    fun getTransactionsByWallet(walletId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions " +
                "WHERE date >= :startTimestamp AND date <= :endTimestamp " +
                "ORDER BY date DESC"
    )
    suspend fun getTransactionsByDateRangeSync(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<TransactionEntity>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactionsWithTags(limit: Int): Flow<List<TransactionWithTags>>
}
package ir.siamak.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {

    @Query("SELECT * FROM installment ORDER BY dueDate ASC")
    fun getAllInstallments(): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM installment WHERE id=:id")
    suspend fun getInstallmentById(id: Long): InstallmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(
        installment: InstallmentEntity
    )

    @Update
    suspend fun updateInstallment(
        installment: InstallmentEntity
    )

    @Delete
    suspend fun deleteInstallment(
        installment: InstallmentEntity
    )


    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end")
    suspend fun getTransactionsByDateRangeSync(start: Long, end: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>
}
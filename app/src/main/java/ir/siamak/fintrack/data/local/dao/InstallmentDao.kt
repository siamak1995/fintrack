package ir.siamak.fintrack.data.local.dao

import androidx.room.*
import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {
    @Query("SELECT * FROM installment ORDER BY dueDate ASC")
    fun getAllInstallments(): Flow<List<InstallmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(installment: InstallmentEntity)

    @Delete
    suspend fun deleteInstallment(installment: InstallmentEntity)
}

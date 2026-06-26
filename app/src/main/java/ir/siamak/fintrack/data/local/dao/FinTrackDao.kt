package ir.siamak.fintrack.data.local.dao

import androidx.room.*
import ir.siamak.fintrack.data.local.entity.ExpenseEntity
import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinTrackDao {

    // Wallet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Delete
    suspend fun deleteWallet(wallet: WalletEntity)

    @Query("SELECT * FROM wallet ORDER BY id DESC")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE id = :walletId LIMIT 1")
    suspend fun getWalletById(walletId: Long): WalletEntity?

    // Expense
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expense ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense WHERE walletId = :walletId ORDER BY date DESC")
    fun getExpensesByWallet(walletId: Long): Flow<List<ExpenseEntity>>

    // Installment
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(installment: InstallmentEntity): Long

    @Update
    suspend fun updateInstallment(installment: InstallmentEntity)

    @Delete
    suspend fun deleteInstallment(installment: InstallmentEntity)

    @Query("SELECT * FROM installment ORDER BY dueDate ASC")
    fun getAllInstallments(): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM installment WHERE id = :installmentId LIMIT 1")
    suspend fun getInstallmentById(installmentId: Long): InstallmentEntity?
}

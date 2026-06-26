package ir.siamak.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.data.local.entity.TransactionEntity
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

    // Transaction
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

package ir.siamak.fintrack.data.local.dao

import androidx.room.*
import ir.siamak.fintrack.data.model.Expense
import ir.siamak.fintrack.data.model.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface FinTrackDao {

    // --- Wallets ---
    @Query("SELECT * FROM wallets")
    fun getAllWallets(): Flow<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    @Query("SELECT * FROM wallets WHERE id = :id LIMIT 1")
    suspend fun getWalletById(id: Long): Wallet?

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE walletId = :walletId")
    fun getExpensesByWallet(walletId: Long): Flow<List<Expense>>
}

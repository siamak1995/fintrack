package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}

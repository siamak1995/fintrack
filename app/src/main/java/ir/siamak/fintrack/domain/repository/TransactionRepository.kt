package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun insert(transaction: Transaction)

    suspend fun getTransactionsByDateRange(fromDate: Long?, toDate: Long?): List<Transaction>

    suspend fun getAllTransactionsSync(): List<Transaction>

}

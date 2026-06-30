package ir.siamak.fintrack.data.repository

import ir.siamak.fintrack.data.local.dao.FinTrackDao
import ir.siamak.fintrack.data.mapper.toEntity
import ir.siamak.fintrack.data.mapper.toModel
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: FinTrackDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { transactions ->
            transactions.map { it.toModel() }
        }
    }

    override fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>> {
        return dao.getTransactionsByWallet(walletId).map { transactions ->
            transactions.map { it.toModel() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun insert(transaction: Transaction) {
        insertTransaction(transaction)
    }
}

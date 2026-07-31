package ir.siamak.fintrack.data.repository

import ir.siamak.fintrack.data.local.dao.TransactionDao
import ir.siamak.fintrack.data.local.entity.TransactionTagCrossRef
import ir.siamak.fintrack.data.mapper.toEntity
import ir.siamak.fintrack.data.mapper.toModel
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {


    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactionsWithTags()
            .map { relations ->
                relations.map { relation -> relation.toModel() }
            }
    }

    override fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>> {
        return dao.getTransactionsByWalletWithTags(walletId).map { relations ->
            relations.map { relation -> relation.toModel() }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return dao.getTransactionById(id)?.toModel()
    }


    override suspend fun insertTransaction(transaction: Transaction): Long {
        val transactionId = dao.insertTransaction(transaction.toEntity())

        val crossRefs = transaction.tags.map { tag ->
            TransactionTagCrossRef(
                transactionId = transactionId,
                tagId = tag.id
            )
        }

        if (crossRefs.isNotEmpty()) {
            dao.insertTransactionTagCrossRefs(crossRefs)
        }

        return transactionId
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())

        dao.deleteTagsForTransaction(transaction.id)

        val crossRefs = transaction.tags.map { tag ->
            TransactionTagCrossRef(
                transactionId = transaction.id,
                tagId = tag.id
            )
        }

        if (crossRefs.isNotEmpty()) {
            dao.insertTransactionTagCrossRefs(crossRefs)
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun insert(transaction: Transaction) {
        insertTransaction(transaction)
    }

    override suspend fun getTransactionsByDateRange(
        fromDate: Long?,
        toDate: Long?
    ): List<Transaction> {
        return dao.getAllTransactionsWithTagsSync().filter {
            val date = it.transaction.date
            val startMatch = fromDate == null || date >= fromDate
            val endMatch = toDate == null || date <= toDate
            startMatch && endMatch
        }.map { it.toModel() }
    }


    override suspend fun getTransactionsByDateRangeSync(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction> {
        return dao.getTransactionsWithTagsByDateRangeSync(startTimestamp, endTimestamp)
            .map { it.toModel() }
    }


    override suspend fun getAllTransactionsSync(): List<Transaction> {
        return dao.getAllTransactionsWithTagsSync()
            .map { it.toModel() }
    }


}

package ir.siamak.fintrack.data.repository

import androidx.room.Query
import ir.siamak.fintrack.data.local.dao.TransactionDao
import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.mapper.toEntity
import ir.siamak.fintrack.data.mapper.toModel
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
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

    override suspend fun getTransactionById(id: Long): Transaction? {
        return dao.getTransactionById(id)?.toModel()
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

    override suspend fun getTransactionsByDateRange(fromDate: Long?, toDate: Long?): List<Transaction> {
        // گرفتن تمام تراکنش‌ها از DAO (تبدیل Flow به لیست یا استفاده از کوئری مستقیم)
        // راه بهینه‌تر این است که یک کوئری در FinTrackDao برای بازه زمانی بنویسی
        // فعلاً فرض می‌کنیم کل لیست را می‌گیریم و فیلتر می‌کنیم (یا کوئری DAO را اضافه می‌کنی)
        return dao.getAllTransactionsSync().filter {
            val date = it.date
            val startMatch = fromDate == null || date >= fromDate
            val endMatch = toDate == null || date <= toDate
            startMatch && endMatch
        }.map { it.toModel() } // تبدیل Entity به Domain Model
    }

    override suspend fun getTransactionsByDateRangeSync(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction> {
        return dao.getTransactionsByDateRangeSync(startTimestamp, endTimestamp)
            .map { it.toModel() }
    }


    override suspend fun getAllTransactionsSync(): List<Transaction> {
        return dao.getAllTransactionsSync().map { it.toModel() }
    }
}

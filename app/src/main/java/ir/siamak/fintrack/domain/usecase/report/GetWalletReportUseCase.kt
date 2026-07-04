package ir.siamak.fintrack.domain.usecase.report

import ir.siamak.fintrack.data.local.dao.TransactionDao
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.report.model.WalletReport
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetWalletReportUseCase @Inject constructor(
    private val dao: TransactionDao
) {
    suspend operator fun invoke(startTimestamp: Long?, endTimestamp: Long?): List<WalletReport> {
        // ۱. دریافت تمامی تراکنش‌های بازه فیلتر شده یا کل تراکنش‌ها
        val transactions = if (startTimestamp != null && endTimestamp != null) {
            dao.getTransactionsByDateRangeSync(startTimestamp, endTimestamp)
        } else {
            dao.getAllTransactionsSync()
        }

        // ۲. دریافت لیست کیف پول‌ها برای متناظر کردن نام و موجودی لحظه‌ای
        val wallets = dao.getAllWallets().first()

        // ۳. گروه‌بندی تراکنش‌ها بر اساس Wallet ID
        val txGroupedByWallet = transactions.groupBy { it.walletId }

        return wallets.map { wallet ->
            val walletTransactions = txGroupedByWallet[wallet.id] ?: emptyList()

            val income = walletTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expense = walletTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            WalletReport(
                walletId = wallet.id,
                walletName = wallet.name,
                walletColor = wallet.color,
                currentBalance = wallet.balance,
                totalIncome = income,
                totalExpense = expense,
                transactionCount = walletTransactions.size
            )
        }
    }
}

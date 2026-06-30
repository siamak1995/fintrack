package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.data.model.Transaction
import java.util.Calendar

sealed class DateGroup {
    object Today : DateGroup()
    object Yesterday : DateGroup()
    object Earlier : DateGroup()
}

class WalletCalculator {

    /**
     * گروه‌بندی تراکنش‌ها به امروز، دیروز و پیش از آن
     */
    fun groupTransactionsByDate(transactions: List<Transaction>): Map<DateGroup, List<Transaction>> {
        val todayStart = getStartOfDay(0)
        val yesterdayStart = getStartOfDay(-1)

        return transactions.groupBy { transaction ->
            val transactionDate = transaction.date // فرض بر این است که تاریخ به صورت Long (Timestamp) ذخیره شده است
            when {
                transactionDate >= todayStart -> DateGroup.Today
                transactionDate >= yesterdayStart && transactionDate < todayStart -> DateGroup.Yesterday
                else -> DateGroup.Earlier
            }
        }
    }

    private fun getStartOfDay(daysOffset: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

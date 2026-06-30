package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType

/**
 * کلاس محاسبه‌گر داشبورد.
 *
 * این کلاس تمام محاسبات مالی مربوط به داشبورد را انجام می‌دهد.
 * هدف این است که منطق مالی از ViewModel و UI جدا شود.
 *
 * تمام مقادیر داشبورد مانند:
 *
 * - مجموع درآمد ماه
 * - مجموع هزینه ماه
 * - مانده ماه
 * - تعداد تراکنش‌ها
 *
 * از طریق این کلاس محاسبه می‌شوند.
 *
 * این کلاس فقط روی داده‌های Transaction کار می‌کند.
 */
class DashboardCalculator {

    /**
     * محاسبه مجموع درآمد.
     */
    fun calculateTotalIncome(
        transactions: List<Transaction>
    ): Double {
        return transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    /**
     * محاسبه مجموع هزینه.
     */
    fun calculateTotalExpense(
        transactions: List<Transaction>
    ): Double {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    /**
     * محاسبه مانده باقی مانده.
     */
    fun calculateRemainingBalance(
        income: Double,
        expense: Double
    ): Double {
        return income - expense
    }

    /**
     * محاسبه تعداد تراکنش‌ها.
     */
    fun calculateTransactionCount(
        transactions: List<Transaction>
    ): Int {
        return transactions.size
    }

}

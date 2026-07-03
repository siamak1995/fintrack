package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType

/**
 * مسئول تمام محاسبات داشبورد.
 *
 * هیچ محاسبه‌ای داخل ViewModel انجام نمی‌شود.
 */
class DashboardCalculator {

    /**
     * مجموع درآمد
     */
    fun income(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    }

    /**
     * مجموع هزینه
     */
    fun expense(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    }

    /**
     * موجودی واقعی
     */
    fun balance(
        transactions: List<Transaction>
    ): Double {

        return income(transactions) - expense(transactions)

    }

    /**
     * آخرین تراکنش‌ها
     */
    fun recentTransactions(
        transactions: List<Transaction>,
        count: Int = 5
    ): List<Transaction> {

        return transactions
            .sortedByDescending { it.date }
            .take(count)

    }

    fun saving(
        transactions: List<Transaction>
    ): Double {

        return income(transactions) - expense(transactions)

    }

    fun spendingPercent(
        transactions: List<Transaction>
    ): Float {

        val income = income(transactions)

        if (income == 0.0)
            return 0f

        return ((expense(transactions) / income) * 100)
            .coerceIn(0.0,100.0)
            .toFloat()

    }

    fun savingPercent(
        transactions: List<Transaction>
    ): Float {

        val income = income(transactions)

        if (income == 0.0)
            return 0f

        return ((saving(transactions) / income) * 100)
            .coerceIn(0.0,100.0)
            .toFloat()

    }


    fun insight(
        transactions: List<Transaction>
    ): String {

        val saving = saving(transactions)

        return when {

            transactions.isEmpty() ->
                "هنوز تراکنشی ثبت نشده است."

            saving > 0 ->
                "عملکرد مالی این ماه مثبت است."

            saving == 0.0 ->
                "درآمد و هزینه برابر است."

            else ->
                "هزینه‌ها از درآمد بیشتر شده‌اند."

        }

    }
}
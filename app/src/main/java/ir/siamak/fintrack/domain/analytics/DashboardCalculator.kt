package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.core.extensions.isToday
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.dashboard.DashboardChart
import ir.siamak.fintrack.domain.dashboard.DashboardMoney
import ir.siamak.fintrack.domain.dashboard.DashboardStatistics

/**
 * مسئول تمام محاسبات داشبورد.
 *
 * این کلاس هیچ وابستگی به UI ندارد و فقط محاسبات Domain را انجام می‌دهد.
 */
class DashboardCalculator {

    fun income(
        transactions: List<Transaction>
    ): Double =
        transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    fun expense(
        transactions: List<Transaction>
    ): Double =
        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    fun balance(
        transactions: List<Transaction>
    ): Double =
        income(transactions) - expense(transactions)

    fun saving(
        transactions: List<Transaction>
    ): Double =
        balance(transactions)

    fun recentTransactions(
        transactions: List<Transaction>,
        count: Int = 5
    ): List<Transaction> =
        transactions
            .sortedByDescending { it.date }
            .take(count)

    fun spendingPercent(
        transactions: List<Transaction>
    ): Float {

        val income = income(transactions)

        if (income == 0.0) return 0f

        return ((expense(transactions) / income) * 100)
            .coerceIn(0.0, 100.0)
            .toFloat()
    }

    fun savingPercent(
        transactions: List<Transaction>
    ): Float {

        val income = income(transactions)

        if (income == 0.0) return 0f

        return ((saving(transactions) / income) * 100)
            .coerceIn(0.0, 100.0)
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

    fun todayIncome(
        transactions: List<Transaction>
    ): Double =
        transactions
            .filter {
                it.type == TransactionType.INCOME &&
                        it.date.isToday()
            }
            .sumOf { it.amount }

    fun todayExpense(
        transactions: List<Transaction>
    ): Double =
        transactions
            .filter {
                it.type == TransactionType.EXPENSE &&
                        it.date.isToday()
            }
            .sumOf { it.amount }

    fun calculateMoney(
        wallets: List<Wallet>,
        transactions: List<Transaction>
    ): DashboardMoney {

        val income = income(transactions)
        val expense = expense(transactions)
        val balance = income - expense

        return DashboardMoney(

            income = income,

            expense = expense,

            saving = balance,

            balance = balance,

            walletBalance = wallets.sumOf { it.balance },

            todayIncome = todayIncome(transactions),

            todayExpense = todayExpense(transactions)

        )
    }

    fun calculateChart(
        transactions: List<Transaction>
    ) = DashboardChart(

        spendingPercent = spendingPercent(transactions),

        savingPercent = savingPercent(transactions)

    )

    fun calculateStatistics(

        wallets: List<Wallet>,

        members: List<Member>,

        installments: List<Installment>,

        transactions: List<Transaction>

    ) = DashboardStatistics(

        walletCount = wallets.size,

        transactionCount = transactions.size,

        memberCount = members.size,

        installmentCount = installments.size

    )

}
package ir.siamak.fintrack.personalaccountant.domain.analytics

import ir.siamak.fintrack.common.core.extensions.isToday
import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.dashboard.DashboardChart
import ir.siamak.fintrack.personalaccountant.domain.dashboard.DashboardMoney
import ir.siamak.fintrack.personalaccountant.domain.dashboard.DashboardStatistics
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.FinancialHealth
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

/**
 * مسئول تمام محاسبات داشبورد
 *
 * هیچ وابستگی به UI ندارد.
 * تمام Business Logic داشبورد در این کلاس قرار می‌گیرد.
 */
class DashboardCalculator @Inject constructor() {

    fun calculateMonthlyMoney(
        wallets: List<Wallet>,
        transactions: List<Transaction>
    ): DashboardMoney {

        val monthly = transactions.filter { it.isCurrentMonth() }

        val income = monthlyIncome(monthly)
        val expense = monthlyExpense(monthly)
        val balance = income - expense

        return DashboardMoney(
            income = income,
            expense = expense,
            saving = balance,
            balance = balance,
            walletBalance = wallets.sumOf { it.balance },
            todayIncome = todayIncome(monthly),
            todayExpense = todayExpense(monthly)
        )
    }

    fun calculateChart(
        transactions: List<Transaction>
    ): DashboardChart {

        val monthly = transactions.filter { it.isCurrentMonth() }

        val income = monthlyIncome(monthly)
        val expense = monthlyExpense(monthly)

        if (income <= 0.0) {
            return DashboardChart(
                spendingPercent = 0f,
                savingPercent = 0f
            )
        }

        val total = income + expense

        return DashboardChart(
            spendingPercent = ((expense * 100) / total)
                .coerceIn(0.0,100.0)
                .toFloat(),
            savingPercent = ((income * 100) / total)
                .coerceIn(0.0,100.0)
                .toFloat()
        )
    }

    fun calculateStatistics(
        wallets: List<Wallet>,
        members: List<Member>,
        installments: List<Installment>,
        transactions: List<Transaction>
    ): DashboardStatistics {

        return DashboardStatistics(
            walletCount = wallets.size,
            transactionCount = transactions.size,
            memberCount = members.size,
            installmentCount = installments.size
        )
    }

    fun recentTransactions(
        transactions: List<Transaction>,
        count: Int = 5
    ): List<Transaction> {

        return transactions
            .sortedByDescending { it.date }
            .take(count)
    }

    fun upcomingInstallments(
        installments: List<Installment>,
        count: Int = 3
    ): List<Installment> {

        return installments
            .filter { !it.isPaid }
            .sortedBy { it.dueDate }
            .take(count)
    }

    fun generateInsight(
        transactions: List<Transaction>,
        installments: List<Installment>
    ): String {

        if (transactions.isEmpty())
            return "اولین تراکنش خود را ثبت کنید."

        val monthly = transactions.filter { it.isCurrentMonth() }

        val income = monthlyIncome(monthly)
        val expense = monthlyExpense(monthly)
        val balance = income - expense
        val upcoming = upcomingInstallments(installments).size

        return when {

            balance > 0 && upcoming == 0 ->
                "وضعیت مالی شما عالی است و هیچ قسط نزدیکی ندارید."

            balance > 0 ->
                "وضعیت مالی مناسب است اما $upcoming قسط پیش رو دارید."

            balance == 0.0 ->
                "درآمد و هزینه این ماه برابر است."

            else ->
                "هزینه‌های این ماه از درآمد بیشتر شده است."
        }
    }

    fun financialHealth(
        transactions: List<Transaction>
    ): FinancialHealth {

        val chart = calculateChart(transactions)

        return when {

            chart.savingPercent >= 70f ->
                FinancialHealth.EXCELLENT

            chart.savingPercent >= 50f ->
                FinancialHealth.GOOD

            chart.spendingPercent < 70f ->
                FinancialHealth.WARNING

            else ->
                FinancialHealth.DANGER
        }
    }

    fun biggestExpense(
        transactions: List<Transaction>
    ): Transaction? {

        return transactions
            .filter {
                it.type == TransactionType.EXPENSE &&
                        it.isCurrentMonth()
            }
            .maxByOrNull {
                it.amount
            }
    }

    fun averageDailyExpense(
        transactions: List<Transaction>
    ): Double {

        val expenses = transactions.filter {
            it.type == TransactionType.EXPENSE &&
                    it.isCurrentMonth()
        }

        if (expenses.isEmpty())
            return 0.0

        val days = expenses
            .map {
                Instant.ofEpochMilli(it.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .distinct()
            .size

        return expenses.sumOf { it.amount } / days
    }

    fun monthlyIncome(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    fun monthlyExpense(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    fun todayIncome(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter {
                it.type == TransactionType.INCOME &&
                        it.date.isToday()
            }
            .sumOf { it.amount }
    }

    fun todayExpense(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter {
                it.type == TransactionType.EXPENSE &&
                        it.date.isToday()
            }
            .sumOf { it.amount }
    }

    private fun Transaction.isCurrentMonth(): Boolean {

        val localDate = Instant
            .ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return YearMonth.from(localDate) == YearMonth.now()
    }
}

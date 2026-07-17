package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.domain.dashboard.DashboardData
import ir.siamak.fintrack.presentation.dashboard.DashboardState
import javax.inject.Inject

class DashboardUiMapper @Inject constructor(
    private val calculator: DashboardCalculator
) {

    fun map(data: DashboardData): DashboardState {

        val money=calculator.calculateMonthlyMoney(
            wallets=data.wallets,
            transactions=data.transactions
        )

        val chart=calculator.calculateChart(
            data.transactions
        )

        val statistics=calculator.calculateStatistics(
            wallets=data.wallets,
            members=data.members,
            installments=data.installments,
            transactions=data.transactions
        )

        return DashboardState(

            wallets=data.wallets,
            members=data.members,

            transactions=data.transactions,
            recentTransactions=calculator.recentTransactions(data.transactions),

            installments=data.installments,
            upcomingInstallments=calculator.upcomingInstallments(data.installments),

            monthlyIncome=money.income,
            monthlyExpense=money.expense,
            totalBalance=money.balance,
            walletBalance=money.walletBalance,
            saving=money.saving,

            todayIncome=money.todayIncome,
            todayExpense=money.todayExpense,

            spendingPercent=chart.spendingPercent,
            savingPercent=chart.savingPercent,

            walletCount=statistics.walletCount,
            memberCount=statistics.memberCount,
            transactionCount=statistics.transactionCount,
            installmentCount=statistics.installmentCount,

            financialHealth=calculator.financialHealth(data.transactions),

            biggestExpense=calculator.biggestExpense(data.transactions),

            averageDailyExpense=calculator.averageDailyExpense(data.transactions),

            insight=calculator.generateInsight(
                transactions=data.transactions,
                installments=data.installments
            ),

            isLoading=false,
            error=null
        )
    }

}
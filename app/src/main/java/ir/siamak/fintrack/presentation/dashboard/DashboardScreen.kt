package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.dashboard.components.DashboardEmpty
import ir.siamak.fintrack.presentation.dashboard.components.DashboardError
import ir.siamak.fintrack.presentation.dashboard.components.DashboardLoading
import ir.siamak.fintrack.presentation.dashboard.components.sections.ChartSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.FinancialSummarySection
import ir.siamak.fintrack.presentation.dashboard.components.sections.GreetingSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.InsightSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.MemberSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.QuickActionsSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.RecentTransactionsSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.UpcomingInstallmentsSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.WalletSection

@Composable
fun DashboardScreen(
    state: DashboardState,
) {

    Scaffold() { padding ->

        when {

            state.isLoading -> {
                DashboardLoading()
                return@Scaffold
            }

            state.error != null -> {
                DashboardError(state.error!!)
                return@Scaffold
            }

            state.wallets.isEmpty()
                    && state.transactions.isEmpty()
                    && state.members.isEmpty()
                    && state.installments.isEmpty() -> {

                DashboardEmpty("هنوز اطلاعاتی ثبت نشده است.")
                return@Scaffold
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                GreetingSection(state.userName, state.insight)
            }

            item {
                FinancialSummarySection(
                    totalBalance = state.totalBalance,
                    income = state.monthlyIncome,
                    expense = state.monthlyExpense,
                    walletBalance = state.walletBalance
                )
            }

            item {
                ChartSection(
                    spending = state.spendingPercent,
                    saving = state.savingPercent
                )
            }

            item { InsightSection(state.insight) }


            item {
                RecentTransactionsSection(state.recentTransactions)
            }
//@TODO - phase-2
//            item {
//                UpcomingInstallmentsSection(state.installments)
//            }

        }
    }
}
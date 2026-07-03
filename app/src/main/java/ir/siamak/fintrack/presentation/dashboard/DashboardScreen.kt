package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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

    onAddTransactionClick: () -> Unit,

    onAddWalletClick: () -> Unit,

    onWalletClick: (Long) -> Unit,

    onMembersClick: () -> Unit,

    onOpenInstallment: () -> Unit,

    onOpenReports: () -> Unit

) {

    Scaffold(

        floatingActionButton = {

            FloatingActionButton(

                onClick = onAddTransactionClick

            ) {

                Icon(
                    Icons.Default.Add,
                    null
                )

            }

        }

    ) { padding ->

        if (state.isLoading) {

            DashboardLoading()

            return@Scaffold

        }

        state.error?.let {

            DashboardError(it)

            return@Scaffold

        }

        val hasAnyData =
            state.wallets.isNotEmpty() ||
                    state.transactions.isNotEmpty() ||
                    state.members.isNotEmpty() ||
                    state.installments.isNotEmpty()

        if (!hasAnyData) {
            DashboardEmpty("هنوز اطلاعاتی ثبت نشده است.")
            return@Scaffold
        }

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(20.dp)

        ) {

            item {
                GreetingSection(
                    userName = state.userName,
                    insight = state.insight
                )
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
                QuickActionsSection(

                    onAddWallet = onAddWalletClick,

                    onAddTransaction = onAddTransactionClick,

                    onOpenMembers = onMembersClick,

                    onOpenInstallment = onOpenInstallment,

                    onOpenReports = onOpenReports
                )
            }

            item {
                ChartSection(

                    spending = state.spendingPercent,

                    saving = state.savingPercent

                )
            }

            item {
                InsightSection(
                    insight = state.insight
                )
            }

            item {
                WalletSection(

                    wallets = state.wallets,

                    onWalletClick = onWalletClick,

                    onAddWalletClick = onAddWalletClick

                )
            }

            item {
                RecentTransactionsSection(
                    transactions = state.recentTransactions
                )
            }

            item {
                UpcomingInstallmentsSection(
                    installments = state.installments
                )
            }

            item {
                MemberSection(

                    members = state.members,

                    onMembersClick = onMembersClick

                )
            }


        }

    }

}
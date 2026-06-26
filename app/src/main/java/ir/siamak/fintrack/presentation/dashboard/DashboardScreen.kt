package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.presentation.dashboard.components.DashboardLoading
import ir.siamak.fintrack.presentation.dashboard.components.sections.InstallmentSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.MemberSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.SummarySection
import ir.siamak.fintrack.presentation.dashboard.components.sections.TransactionSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.WalletSection

/**
 * صفحه اصلی داشبورد برنامه.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onAddWalletClick: () -> Unit,
    onWalletClick: (Long) -> Unit,
    onAddTransactionClick: () -> Unit,
    onMembersClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "افزودن تراکنش"
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            DashboardLoading()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    SummarySection(
                        totalBalance = state.totalBalance,
                        income = state.monthlyIncome,
                        expense = state.monthlyExpense
                    )
                }

                item {
                    WalletSection(
                        wallets = state.wallets,
                        onWalletClick = onWalletClick
                    )
                }

                item {
                    TransactionSection(
                        transactions = state.recentTransactions
                    )
                }

                item {
                    MemberSection(
                        members = state.members
                    )
                }

                item {
                    InstallmentSection(
                        installments = state.installments
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

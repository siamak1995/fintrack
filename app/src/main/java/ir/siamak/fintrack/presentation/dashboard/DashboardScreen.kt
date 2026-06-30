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
import ir.siamak.fintrack.presentation.dashboard.components.sections.SummarySection
import ir.siamak.fintrack.presentation.dashboard.components.sections.TransactionSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.WalletSection

/**
 * صفحه اصلی داشبورد برنامه.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onWalletClick: (Long) -> Unit,
    onAddTransactionClick: () -> Unit,
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
                        income = state.totalIncome,
                        expense = state.totalExpense,
                        totalBalance = state.currentBalance
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
                        transactions = state.transactions.take(5)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

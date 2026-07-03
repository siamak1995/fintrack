package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Route صفحه داشبورد.
 *
 * مسئول اتصال ViewModel به UI است.
 */
@Composable
fun DashboardRoute(

    onAddTransactionClick: () -> Unit,
    onAddWalletClick: () -> Unit,
    onWalletClick: (Long) -> Unit,
    onMembersClick: () -> Unit,
    onInstallmentClick: () -> Unit,
    onReportsClick: () -> Unit,

    viewModel: DashboardViewModel = hiltViewModel()

) {

    val state by viewModel.state.collectAsState()

    DashboardScreen(

        state = state,

        onAddTransactionClick = onAddTransactionClick,
        onAddWalletClick = onAddWalletClick,
        onWalletClick = onWalletClick,
        onOpenInstallment = onInstallmentClick,
        onMembersClick = onMembersClick,
        onOpenReports = onReportsClick
    )

}
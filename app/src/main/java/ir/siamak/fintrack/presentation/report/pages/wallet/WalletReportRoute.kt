package ir.siamak.fintrack.presentation.report.pages.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalletReportRoute(
    onBackClick: () -> Unit,
    viewModel: WalletReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WalletReportScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSelectDateRangeClick = viewModel::showDatePicker,
        onClearDateRangeClick = viewModel::clearDateRange,
        onDismissDatePicker = viewModel::hideDatePicker,
        onConfirmDateRange = viewModel::onDateRangeSelected
    )
}

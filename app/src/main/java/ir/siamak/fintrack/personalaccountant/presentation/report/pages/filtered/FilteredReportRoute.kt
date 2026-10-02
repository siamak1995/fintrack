package ir.siamak.fintrack.personalaccountant.presentation.report.pages.filtered

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FilteredReportRoute(
    onBackClick: () -> Unit,
    viewModel: FilteredReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FilteredReportScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onMemberSelected = viewModel::onMemberSelected,
        onWalletSelected = viewModel::onWalletSelected,
        onTypeSelected = viewModel::onTypeSelected,
        onSelectDateRangeClick = viewModel::showDatePicker,
        onClearDateRangeClick = viewModel::clearDateRange,
        onResetFiltersClick = viewModel::resetAllFilters,
        onDismissDatePicker = viewModel::hideDatePicker,
        onConfirmDateRange = viewModel::onDateRangeSelected
    )
}


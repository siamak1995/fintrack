package ir.siamak.fintrack.personalaccountant.presentation.report.pages.transactionsHistory

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Route-level component for the transaction history report.
 *
 * This composable obtains [HistoryReportViewModel] from Hilt, observes its
 * state with lifecycle awareness, and connects ViewModel events to the
 * stateless [HistoryReportScreen].
 *
 * Navigation code should call this component instead of calling
 * [HistoryReportScreen] directly.
 *
 * @param onBackClick Called when the user requests navigation to the
 * previous screen.
 * @param viewModel ViewModel supplied by Hilt for the current navigation
 * destination.
 */
@Composable
fun HistoryReportRoute(
    onBackClick: () -> Unit,
    viewModel: HistoryReportViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    HistoryReportScreen(
        uiState = uiState.value,
        onBackClick = onBackClick,
        onShowDatePicker = viewModel::showDatePicker,
        onHideDatePicker = viewModel::hideDatePicker,
        onClearDateRange = viewModel::clearDateRange,
        onDateRangeSelected = viewModel::onDateRangeSelected,
        onTypeSelected = viewModel::onTypeSelected,
        onTagClick = viewModel::toggleTag
    )
}



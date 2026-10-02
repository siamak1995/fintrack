package ir.siamak.fintrack.personalaccountant.presentation.report.pages.member

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Route entry for Member Report screen.
 *
 * این کامپوننت ViewModel را از Hilt می‌گیرد و State/Actionها را
 * به اسکرین نمایشی متصل می‌کند.
 *
 * @param onBackClick اکشن بازگشت
 */
@Composable
fun MemberReportRoute(
    onBackClick: () -> Unit,
    viewModel: MemberReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MemberReportScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSelectDateRangeClick = viewModel::showDatePicker,
        onClearDateRangeClick = viewModel::clearDateRange,
        onDismissDatePicker = viewModel::hideDatePicker,
        onConfirmDateRange = viewModel::onDateRangeSelected,
    )
}


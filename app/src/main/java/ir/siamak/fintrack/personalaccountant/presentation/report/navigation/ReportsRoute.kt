package ir.siamak.fintrack.personalaccountant.presentation.report.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.personalaccountant.presentation.report.ReportViewModel
import ir.siamak.fintrack.personalaccountant.presentation.report.ReportsScreen

@Composable
fun ReportsRoute(
    onMemberReportClick: () -> Unit,
    onWalletReportClick: () -> Unit,
    onHistoryReportClick: () -> Unit,
    onFilteredReportClick: () -> Unit,
    onVisualReportClick: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReportsScreen(
        state = state,
        onMemberReportClick = onMemberReportClick,
        onWalletReportClick = onWalletReportClick,
        onHistoryReportClick = onHistoryReportClick,
        onFilteredReportClick = onFilteredReportClick,
        onVisualReportClick = onVisualReportClick
    )
}


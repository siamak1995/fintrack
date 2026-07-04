package ir.siamak.fintrack.presentation.report.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// اگر ReportsRoute را جابجا نکردی، همین ایمپورت درسته:
import ir.siamak.fintrack.presentation.report.navigation.ReportsRoute

const val REPORTS_ROUTE = "reports"

fun NavGraphBuilder.reportsGraph(
    onNavigateToMember: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFiltered: () -> Unit,
    onNavigateToVisual: () -> Unit
) {
    composable(REPORTS_ROUTE) {
        ReportsRoute(
            onMemberReportClick = onNavigateToMember,
            onWalletReportClick = onNavigateToWallet,
            onHistoryReportClick = onNavigateToHistory,
            onFilteredReportClick = onNavigateToFiltered,
            onVisualReportClick = onNavigateToVisual
        )
    }
}

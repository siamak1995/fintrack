package ir.siamak.fintrack.presentation.reports.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.siamak.fintrack.presentation.report.navigation.ReportsRoute

const val REPORTS_ROUTE = "reports"

fun NavGraphBuilder.reportsGraph() {

    composable(REPORTS_ROUTE) {

        ReportsRoute()

    }

}
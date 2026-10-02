package ir.siamak.fintrack.personalaccountant.presentation.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardScreen
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardViewModel

/**
 * Route صفحه داشبورد.
 *
 * مسئول اتصال ViewModel به UI است.
 */
@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    DashboardScreen(state = state)

}

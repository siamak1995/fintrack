package ir.siamak.fintrack.presentation.report.navigation


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.presentation.report.ReportsScreen
import ir.siamak.fintrack.presentation.report.ReportsViewModel

@Composable
fun ReportsRoute(

    viewModel: ReportsViewModel =
        hiltViewModel()

) {

    val state by
    viewModel.state.collectAsStateWithLifecycle()

    ReportsScreen(

        state = state

    )

}
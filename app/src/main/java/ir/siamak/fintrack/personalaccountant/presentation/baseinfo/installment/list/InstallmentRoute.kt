package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Route مربوط به صفحه اقساط.
 *
 * این فایل بین ViewModel و Screen ارتباط برقرار می‌کند.
 */
@Composable
fun InstallmentRoute(
    onAddInstallmentClick: () -> Unit,
    onEditInstallmentClick: (Long) -> Unit,
    viewModel: InstallmentViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    InstallmentScreen(
        state = state,
        onAddInstallmentClick = onAddInstallmentClick,
        onEditInstallmentClick = onEditInstallmentClick
    )
}


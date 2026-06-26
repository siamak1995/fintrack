package ir.siamak.fintrack.presentation.installment.list

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

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

    val state = viewModel.state.value

    InstallmentScreen(
        state = state,
        onAddInstallmentClick = onAddInstallmentClick,
        onEditInstallmentClick = onEditInstallmentClick
    )
}

package ir.siamak.fintrack.presentation.wallet.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Route صفحه حساب‌ها.
 *
 * این تابع لایه اتصال بین ViewModel و UI خالص صفحه است.
 * در اینجا state از ViewModel دریافت می‌شود و callbackهای لازم به Screen پاس داده می‌شوند.
 *
 * مزیت این الگو:
 * - خود Screen کاملاً stateless می‌شود
 * - تست و preview راحت‌تر می‌شود
 * - وابستگی مستقیم UI به Hilt/ViewModel کمتر می‌شود
 *
 * @param onAddWalletClick هدایت به صفحه افزودن حساب
 * @param onEditWalletClick هدایت به صفحه ویرایش حساب
 */
@Composable
fun WalletRoute(
    onAddWalletClick: () -> Unit,
    onEditWalletClick: (Long) -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WalletScreen(
        uiState = uiState,
        onAddWalletClick = onAddWalletClick,
        onEditWalletClick = onEditWalletClick,
        onDeleteWalletClick = viewModel::deleteWallet
    )
}

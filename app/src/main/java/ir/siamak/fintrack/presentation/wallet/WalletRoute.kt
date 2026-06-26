package ir.siamak.fintrack.presentation.wallet

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * لایه Route برای صفحه Wallet.
 *
 * این تابع مسئول دریافت ViewModel از Hilt و ارسال آن
 * به کامپوزبل نمایشی صفحه است.
 */
@Composable
fun WalletRoute(
    viewModel: WalletViewModel = hiltViewModel()
) {
    WalletScreen(viewModel = viewModel)
}

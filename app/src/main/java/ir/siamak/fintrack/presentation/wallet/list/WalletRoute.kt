package ir.siamak.fintrack.presentation.wallet.list

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * لایه Route برای صفحه Wallet.
 *
 * این تابع مسئول دریافت ViewModel از Hilt و ارسال آن
 * به کامپوزبل نمایشی صفحه است.
 *
 * @param onBack callback برای بازگشت به صفحه قبل
 * @param viewModel ویومدل صفحه لیست کیف ‌پول‌ها
 */
@Composable
fun WalletRoute(
    onBack: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    WalletScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}

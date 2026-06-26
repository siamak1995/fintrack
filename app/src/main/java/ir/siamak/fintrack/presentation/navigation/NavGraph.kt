package ir.siamak.fintrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ir.siamak.fintrack.presentation.dashboard.DashboardScreen
import ir.siamak.fintrack.presentation.wallet.add_edit_wallet.AddEditWalletScreen
import ir.siamak.fintrack.presentation.wallet.list.WalletRoute

/**
 * گراف ناوبری اپلیکیشن FinTrack.
 *
 * @param navController کنترل‌کننده ناوبری که در سطح Activity تعریف می‌شود.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard
    ) {
        // صفحه داشبورد
        composable<Screen.Dashboard> {
            DashboardScreen(
                onAddWalletClick = {
                    navController.navigate(Screen.AddEditWallet())
                },
                onWalletClick = { walletId ->
                    navController.navigate(Screen.AddEditWallet(walletId))
                }
            )
        }

        // صفحه افزودن/ویرایش کیف ‌پول
        composable<Screen.AddEditWallet> {
            AddEditWalletScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // صفحه لیست کیف ‌پول‌ها
        composable<Screen.WalletList> {
            WalletRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

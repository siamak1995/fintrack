package ir.siamak.fintrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.siamak.fintrack.presentation.dashboard.DashboardScreen
import ir.siamak.fintrack.presentation.dashboard.DashboardViewModel

/**
 * مدیریت سیستم ناوبری (Navigation) کل اپلیکیشن.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            // ایجاد یا دریافت ویومدل داشبورد با استفاده از Hilt
            val viewModel: DashboardViewModel = hiltViewModel()

            DashboardScreen(
                viewModel = viewModel,
                onAddWalletClick = {
                    // فعلاً مسیری برای رفتن نداریم، اینجا را بعداً پر می‌کنیم
                    // navController.navigate(Screen.AddWallet.route)
                }
            )
        }
    }
}

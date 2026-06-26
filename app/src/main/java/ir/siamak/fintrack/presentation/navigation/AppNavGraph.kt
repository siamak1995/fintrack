package ir.siamak.fintrack.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import ir.siamak.fintrack.presentation.components.FTBottomBar
import ir.siamak.fintrack.presentation.dashboard.DashboardScreen
import ir.siamak.fintrack.presentation.member.add_edit_member.AddEditMemberScreen
import ir.siamak.fintrack.presentation.transaction.add_edit_transaction.AddEditTransactionScreen
import ir.siamak.fintrack.presentation.wallet.add_edit_wallet.AddEditWalletScreen
import ir.siamak.fintrack.presentation.wallet.list.WalletRoute
import ir.siamak.fintrack.presentation.member.list.MemberRoute


/**
 * گراف اصلی ناوبری اپلیکیشن.
 *
 * این تابع مسیرهای اصلی و فرعی برنامه را تعریف می‌کند و مشخص می‌کند
 * bottom bar در چه صفحاتی نمایش داده شود.
 *
 * @param navController کنترلر ناوبری برنامه
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentBottomScreen = when {
        currentDestination?.hasRoute<Screen.Dashboard>() == true -> Screen.Dashboard
        currentDestination?.hasRoute<Screen.WalletList>() == true -> Screen.WalletList
        currentDestination?.hasRoute<Screen.Members>() == true -> Screen.Members
        currentDestination?.hasRoute<Screen.Installments>() == true -> Screen.Installments
        currentDestination?.hasRoute<Screen.Reports>() == true -> Screen.Reports
        else -> null
    }

    val showBottomBar = currentBottomScreen != null

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    FTBottomBar(
                        currentScreen = currentBottomScreen,
                        onItemClick = { screen ->
                            navController.navigate(screen) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard,
                modifier = Modifier.padding(innerPadding)
            ) {
                /**
                 * صفحه اصلی داشبورد.
                 */
                composable<Screen.Dashboard> {
                    DashboardScreen(
                        onAddWalletClick = {
                            navController.navigate(Screen.AddEditWallet())
                        },
                        onWalletClick = { walletId ->
                            navController.navigate(Screen.AddEditWallet(walletId))
                        },
                        onAddTransactionClick = {
                            navController.navigate(Screen.AddEditTransaction())
                        },
                        onMembersClick = {
                            navController.navigate(Screen.Members)
                        }
                    )
                }

                /**
                 * صفحه لیست حساب‌ها.
                 */
                composable<Screen.WalletList> {
                    WalletRoute(
                        onAddWalletClick = {
                            navController.navigate(Screen.AddEditWallet())
                        },
                        onEditWalletClick = { walletId ->
                            navController.navigate(Screen.AddEditWallet(walletId))
                        }
                    )
                }

                /**
                 * صفحه افزودن یا ویرایش حساب.
                 */
                composable<Screen.AddEditWallet> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.AddEditWallet>()

                    AddEditWalletScreen(
                        walletId = args.walletId,
                        onBack = { navController.popBackStack() }
                    )
                }



                /**
                 * صفحه اقساط.
                 */
                composable<Screen.Installments> {
                    PlaceholderScreen(title = "اقساط")
                }

                /**
                 * صفحه گزارشات.
                 */
                composable<Screen.Reports> {
                    PlaceholderScreen(title = "گزارشات")
                }

                /**
                 * صفحه تنظیمات.
                 */
                composable<Screen.Settings> {
                    PlaceholderScreen(title = "تنظیمات")
                }

                /**
                 * صفحه افزودن یا ویرایش تراکنش.
                 */
                composable<Screen.AddEditTransaction> {
                    AddEditTransactionScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                /**
                 * صفحه اعضا.
                 */
                composable<Screen.Members> {
                    MemberRoute(
                        onAddMemberClick = {
                            navController.navigate(Screen.AddEditMember())
                        },
                        onEditMemberClick = { memberId ->
                            navController.navigate(Screen.AddEditMember(memberId))
                        }
                    )
                }
                /**
                 * صفحه افزودن یا ویرایش اعضا.
                 */
                composable<Screen.AddEditMember> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.AddEditMember>()
                    AddEditMemberScreen(
                        memberId = args.memberId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

/**
 * صفحه placeholder برای بخش‌هایی که هنوز UI نهایی آن‌ها پیاده‌سازی نشده است.
 *
 * @param title عنوان بخشی که موقتاً نمایش داده می‌شود
 */
@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "صفحه $title")
    }
}

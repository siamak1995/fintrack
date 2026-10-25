package ir.siamak.fintrack.personalaccountant.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import ir.siamak.fintrack.store.presentation.components.StoreDesign
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.BaseInfoScreen
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.add_edit_installment.AddEditInstallmentsScreen
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.list.InstallmentRoute
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.member.add_edit_member.AddEditMemberScreen
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.member.list.MemberRoute
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.add_edit_tag.AddEditTagScreen
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.add_edit_tag.AddEditTagViewModel
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.list.TagListScreen
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.wallet.add_edit_wallet.AddEditWalletScreen
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.wallet.list.WalletRoute
import ir.siamak.fintrack.personalaccountant.presentation.components.FTBottomBar
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.navigation.DashboardRoute
import ir.siamak.fintrack.personalaccountant.presentation.landing.LandingScreen
import ir.siamak.fintrack.personalaccountant.presentation.report.navigation.ReportsRoute
import ir.siamak.fintrack.personalaccountant.presentation.report.pages.filtered.FilteredReportRoute
import ir.siamak.fintrack.personalaccountant.presentation.report.pages.member.MemberReportRoute
import ir.siamak.fintrack.personalaccountant.presentation.report.pages.transactionsHistory.HistoryReportRoute
import ir.siamak.fintrack.personalaccountant.presentation.report.pages.visual.VisualReportScreen
import ir.siamak.fintrack.personalaccountant.presentation.report.pages.wallet.WalletReportRoute
import ir.siamak.fintrack.personalaccountant.presentation.transaction.add_edit_transaction.AddEditTransactionScreen
import ir.siamak.fintrack.personalaccountant.presentation.transaction.navigation.TransactionListRoute
import ir.siamak.fintrack.store.presentation.navigation.StoreBottomBar
import ir.siamak.fintrack.store.presentation.navigation.StoreScreen
import ir.siamak.fintrack.store.presentation.navigation.storeNavGraph


/**
 * گراف اصلی ناوبری اپلیکیشن.
 *
 * این تابع مسیرهای اصلی و فرعی برنامه را تعریف می‌کند و مشخص می‌کند
 * bottom bar در چه صفحاتی نمایش داده شود.
 *
 * @param navController کنترلر ناوبری برنامه
 */
@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
fun AppNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentBottomScreen = when {
        currentDestination?.hasRoute<Screen.Dashboard>() == true -> Screen.Dashboard
        currentDestination?.hasRoute<Screen.BaseInfo>() == true -> Screen.BaseInfo
        currentDestination?.hasRoute<Screen.Transactions>() == true -> Screen.Transactions
        currentDestination?.hasRoute<Screen.Reports>() == true -> Screen.Reports
        else -> null
    }


    val showBottomBar = currentBottomScreen != null

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        StoreDesign(currentDestination?.route?.startsWith("store_") == true) {
        Scaffold(
            topBar = {
                val rawRoute = currentDestination?.route
                val route = if(rawRoute?.startsWith(StoreScreen.StoreBaseInfo.route + "/") == true || rawRoute == StoreScreen.StoreMaterials.route) StoreScreen.StoreBaseInfo.route else rawRoute
                val storeRoot = route in listOf(StoreScreen.StoreDashboard.route, StoreScreen.StoreBaseInfo.route,
                    StoreScreen.StoreSales.route, StoreScreen.StoreReports.route, StoreScreen.StorePreOrders.route)
                if(showBottomBar || storeRoot) TopAppBar(
                    title = { Text(if(storeRoot) "حسابدار فروشگاه" else "حسابدار شخصی") },
                    navigationIcon = { IconButton(onClick = {
                        navController.navigate(Screen.Landing) { popUpTo(navController.graph.id); launchSingleTop = true }
                    }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack,"انتخاب حسابدار") } },
                    actions = { IconButton(onClick = { navController.navigate(Screen.Settings) { launchSingleTop = true } }) {
                        Icon(Icons.Outlined.Settings,"تنظیمات و پشتیبان‌گیری")
                    } }
                )
            },
            bottomBar = {
                val rawRoute = currentDestination?.route
                val currentRoute = if(rawRoute?.startsWith(StoreScreen.StoreBaseInfo.route + "/") == true || rawRoute == StoreScreen.StoreMaterials.route) StoreScreen.StoreBaseInfo.route else rawRoute
                val isStoreScreen = currentRoute in listOf(
                    StoreScreen.StoreDashboard.route,
                    StoreScreen.StoreBaseInfo.route,
                    StoreScreen.StoreSales.route,
                    StoreScreen.StoreReports.route,
                    StoreScreen.StorePreOrders.route
                )

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
                } else if (isStoreScreen) {
                    StoreBottomBar(
                        currentRoute = currentRoute,
                        onItemClick = { screen ->
                            navController.navigate(screen.route) {
                                popUpTo(StoreScreen.StoreDashboard.route) {
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Landing,
                modifier = Modifier.padding(innerPadding)
            ) {
                /**
                 * صفحه اصلی .
                 */
                composable<Screen.Landing> {

                    LandingScreen(

                        onEnterDashboard = {

                            navController.navigate(Screen.Dashboard) {

                                popUpTo(Screen.Landing) {
                                    inclusive = false
                                }

                            }

                        },
                        onEnterStoreDashboard = {
                            navController.navigate(StoreScreen.StoreDashboard.route) {
                                popUpTo(Screen.Landing) {
                                    inclusive = false
                                }
                            }
                        }

                    )

                }
                /**
                 * صفحه اصلی داشبورد.
                 */
                composable<Screen.Dashboard> {
                    DashboardRoute(
                        onAction = { action ->
                            when(action) {
                                ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.AddWallet -> navController.navigate(Screen.AddEditWallet())
                                ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.AddTransaction -> navController.navigate(Screen.AddEditTransaction())
                                ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.OpenMembers -> navController.navigate(Screen.Members)
                                ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.OpenReports -> navController.navigate(Screen.Reports)
                                ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.OpenTransactions -> navController.navigate(Screen.Transactions)
                                ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.OpenInstallments -> navController.navigate(Screen.Installments)
                                is ir.siamak.fintrack.personalaccountant.presentation.dashboard.DashboardUiAction.EditWallet -> navController.navigate(Screen.AddEditWallet(action.id))
                            }
                        }
                    )
                }

                /**
                 * ثبت اطلاعات.
                 */
                composable<Screen.BaseInfo> {
                    BaseInfoScreen(
                        onWalletClick = {
                            navController.navigate(Screen.WalletList)
                        },
                        onMemberClick = {
                            navController.navigate(Screen.Members)
                        },
                        //@TODO - phase-2
//        onInstallmentClick = {
//            navController.navigate(Screen.Installments)
//        },
                        onTagClick = {
                            navController.navigate(Screen.Tags)
                        },
                        onSettingsClick = {
                            navController.navigate(Screen.Settings)
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
                 * صفحه اقساط
                 */
                composable<Screen.Installments> {
                    InstallmentRoute(
                        onAddInstallmentClick = {
                            navController.navigate(Screen.AddEditInstallments())
                        },
                        onEditInstallmentClick = { installmentId ->
                            navController.navigate(
                                Screen.AddEditInstallments(installmentId)
                            )
                        }
                    )
                }

                /**
                 * صفحه افزودن یا ویرایش قسط
                 */
                composable<Screen.AddEditInstallments> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.AddEditInstallments>()

                    AddEditInstallmentsScreen(
                        installmentId = args.installmentId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.Tags> {
                    TagListScreen(
                        viewModel = hiltViewModel(),
                        onBack = { navController.popBackStack() },
                        onAddTag = {
                            // برای افزودن تگ جدید، پارامتر را خالی (null) می‌فرستیم
                            navController.navigate(Screen.AddEditTag(tagId = null))
                        },
                        onEditTag = { id ->
                            // برای ویرایش تگ، شناسه آن را پاس می‌دهیم
                            navController.navigate(Screen.AddEditTag(tagId = id))
                        }
                    )
                }

                composable<Screen.AddEditTag> { entry ->
                    val vm: AddEditTagViewModel = hiltViewModel()
                    val args = entry.toRoute<Screen.AddEditTag>()
                    val id = args.tagId

                    LaunchedEffect(id) {
                        vm.loadTag(id)
                    }

                    AddEditTagScreen(
                        tagId = id,
                        viewModel = vm,
                        onBack = { navController.popBackStack() }
                    )
                }



                /**
                 * صفحه گزارشات.
                 */
                composable<Screen.Reports> {
                    ReportsRoute(
                        onMemberReportClick = { navController.navigate(Screen.MemberReport) },
                        onWalletReportClick = { navController.navigate(Screen.WalletReport) },
                        onHistoryReportClick = { navController.navigate(Screen.HistoryReport) },
                        onFilteredReportClick = { navController.navigate(Screen.FilteredReport) },
                        onVisualReportClick = { navController.navigate(Screen.VisualReport) }
                    )
                }

                composable<Screen.WalletReport> {
                    WalletReportRoute(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<Screen.MemberReport> {
                    MemberReportRoute(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<Screen.HistoryReport> {
                    HistoryReportRoute(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<Screen.FilteredReport> {
                    FilteredReportRoute(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<Screen.VisualReport> {
                    VisualReportScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                /**
                 * صفحه تنظیمات.
                 */
                composable<Screen.Settings> {
                    ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.navigation.SettingsRoute(
                        onBackClick = { navController.popBackStack() }
                    )
                }


                /**
                 * صفحه افزودن یا ویرایش تراکنش.
                 */
                composable<Screen.AddEditTransaction> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.AddEditTransaction>()

                    AddEditTransactionScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.Transactions> {
                    TransactionListRoute(
                        onBack = { navController.popBackStack() },
                        onAddTransaction = {
                            navController.navigate(Screen.AddEditTransaction())
                        },
                        onEditTransaction = { transactionId ->
                            navController.navigate(Screen.AddEditTransaction(transactionId))
                        }
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

                storeNavGraph(navController)
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


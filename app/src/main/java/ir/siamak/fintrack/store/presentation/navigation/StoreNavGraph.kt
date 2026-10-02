package ir.siamak.fintrack.store.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ir.siamak.fintrack.store.presentation.baseinfo.BaseInfoHubScreen
import ir.siamak.fintrack.store.presentation.baseinfo.material.MaterialListScreen
import ir.siamak.fintrack.store.presentation.dashboard.StoreDashboardScreen
import ir.siamak.fintrack.store.presentation.invoices.PreOrderInvoiceListScreen
import ir.siamak.fintrack.store.presentation.invoices.SaleInvoiceListScreen
import ir.siamak.fintrack.store.presentation.preorder.PreOrderListScreen
import ir.siamak.fintrack.store.presentation.sales.list.SalesScreen

/** Adds all store routes to the host application's navigation graph. */
fun NavGraphBuilder.storeNavGraph(navController: NavHostController) {
    composable(StoreScreen.StoreDashboard.route) { StoreDashboardScreen(onSales = { navController.navigate(StoreScreen.StoreSales.route) }, onPreOrders = { navController.navigate(StoreScreen.StorePreOrders.route) }) }
    composable(StoreScreen.StoreBaseInfo.route) { BaseInfoHubScreen() }
    composable(StoreScreen.StoreSales.route) { SalesScreen(onInvoices = { navController.navigate(StoreScreen.StoreSaleInvoices.route) }) }
    composable(StoreScreen.StoreMaterials.route) { MaterialListScreen() }
    composable(StoreScreen.StorePreOrders.route) { PreOrderListScreen(onInvoices = { navController.navigate(StoreScreen.StorePreOrderInvoices.route) }) }
    composable(StoreScreen.StoreSaleInvoices.route) { SaleInvoiceListScreen() }
    composable(StoreScreen.StorePreOrderInvoices.route) { PreOrderInvoiceListScreen() }
}

package ir.siamak.fintrack.store.presentation.navigation
import androidx.navigation.*
import androidx.navigation.compose.composable
import ir.siamak.fintrack.store.presentation.baseinfo.BaseInfoHubScreen
import ir.siamak.fintrack.store.presentation.baseinfo.material.MaterialListScreen
import ir.siamak.fintrack.store.presentation.dashboard.StoreDashboardScreen
import ir.siamak.fintrack.store.presentation.sales.list.SalesScreen
import ir.siamak.fintrack.store.presentation.sales.create.CreateSaleScreen
import ir.siamak.fintrack.store.presentation.preorder.list.PreOrderListScreen
import ir.siamak.fintrack.store.presentation.preorder.create.CreatePreOrderScreen
import ir.siamak.fintrack.store.presentation.invoices.sale.SaleInvoiceListScreen
import ir.siamak.fintrack.store.presentation.invoices.sale.SaleInvoiceDetailScreen
import ir.siamak.fintrack.store.presentation.invoices.preorder.PreOrderInvoiceListScreen

private const val CREATE_SALE = "store_create_sale"
private const val CREATE_ORDER = "store_create_order"
private const val DETAIL = "store_invoice/{id}"
fun NavGraphBuilder.storeNavGraph(navController: NavHostController) {
    fun detail(id: Long) { navController.navigate("store_invoice/" + id) }
    composable(StoreScreen.StoreDashboard.route) { StoreDashboardScreen(
        onSales = { navController.navigate(StoreScreen.StoreSales.route) }, onPreOrders = { navController.navigate(StoreScreen.StorePreOrders.route) },
        onReports = { navController.navigate(StoreScreen.StoreReports.route) },
        onCatalog = { navController.navigate(StoreScreen.StoreBaseInfo.route) },
        onMaterials = { navController.navigate(StoreScreen.StoreMaterials.route) }) }
    composable(StoreScreen.StoreReports.route) {
        ir.siamak.fintrack.store.presentation.reports.StoreReportsScreen(::detail, { navController.popBackStack() })
    }
    composable(StoreScreen.StoreBaseInfo.route) { BaseInfoHubScreen() }
    composable(StoreScreen.StoreMaterials.route) { MaterialListScreen() }
    composable(StoreScreen.StoreSales.route) { SalesScreen(
        onInvoices = { navController.navigate(StoreScreen.StoreSaleInvoices.route) }, onCreate = { navController.navigate(CREATE_SALE) }) }
    composable(StoreScreen.StorePreOrders.route) { PreOrderListScreen(
        onInvoices = { navController.navigate(StoreScreen.StorePreOrderInvoices.route) }, onCreate = { navController.navigate(CREATE_ORDER) }) }
    composable(StoreScreen.StoreSaleInvoices.route) { SaleInvoiceListScreen(::detail) }
    composable(StoreScreen.StorePreOrderInvoices.route) { PreOrderInvoiceListScreen(::detail) }
    composable(CREATE_SALE) { CreateSaleScreen(onComplete = { id ->
        navController.navigate("store_invoice/" + id) { popUpTo(CREATE_SALE) { inclusive = true } }
    }) }
    composable(CREATE_ORDER) { CreatePreOrderScreen(onComplete = { id ->
        navController.navigate("store_invoice/" + id) { popUpTo(CREATE_ORDER) { inclusive = true } }
    }) }
    composable(DETAIL, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
        SaleInvoiceDetailScreen(requireNotNull(entry.arguments).getLong("id"))
    }
}

package ir.siamak.fintrack.store.presentation.navigation

/** Routes owned by the isolated store-accounting flow. */
sealed class StoreScreen(val route: String) {
    data object StoreDashboard : StoreScreen("store_dashboard")
    data object StoreBaseInfo : StoreScreen("store_base_info")
    data object StoreSales : StoreScreen("store_sales")
    data object StoreMaterials : StoreScreen("store_materials")
    data object StorePreOrders : StoreScreen("store_pre_orders")
    data object StoreSaleInvoices : StoreScreen("store_sale_invoices")
    data object StorePreOrderInvoices : StoreScreen("store_preorder_invoices")
}

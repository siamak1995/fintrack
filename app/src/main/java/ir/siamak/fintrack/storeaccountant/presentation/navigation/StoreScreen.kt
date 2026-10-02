package ir.siamak.fintrack.storeaccountant.presentation.navigation

/**
 * قرارداد ناوبری بخش حسابدار فروشگاه بر اساس مستندات Master Prompt.
 */
sealed class StoreScreen(val route: String) {
    object StoreDashboard : StoreScreen("StoreDashboard")
    object StoreBaseInfo : StoreScreen("StoreBaseInfo")
    object StoreSales : StoreScreen("StoreSales")
    object StoreMaterials : StoreScreen("StoreMaterials")
    object StorePreOrders : StoreScreen("StorePreOrders")
    object StoreSaleInvoices : StoreScreen("StoreSaleInvoices")
    object StorePreOrderInvoices : StoreScreen("StorePreOrderInvoices")
}


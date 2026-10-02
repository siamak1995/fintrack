package ir.siamak.fintrack.storeaccountant.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import ir.siamak.fintrack.storeaccountant.presentation.dashboard.StoreDashboardScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.store.StoreInfoScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.BaseInfoHubScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.material.MaterialListScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.material.AddMaterialScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller.SellerListScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller.AddSellerScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.product.ProductListScreen
import ir.siamak.fintrack.storeaccountant.presentation.baseinfo.product.AddProductScreen

import ir.siamak.fintrack.storeaccountant.presentation.sales.list.SalesScreen

/**
 * گراف ناوبری بخش حسابدار فروشگاه.
 */
fun NavGraphBuilder.storeNavGraph(navController: NavHostController) {
    composable(StoreScreen.StoreDashboard.route) {
        StoreDashboardScreen()
    }
    
    composable(StoreScreen.StoreBaseInfo.route) {
        BaseInfoHubScreen(
            onStoreInfoClick = { navController.navigate("StoreInfo") },
            onSellerManagementClick = { navController.navigate("SellerList") },
            onProductManagementClick = { navController.navigate("ProductList") },
            onMaterialManagementClick = { navController.navigate(StoreScreen.StoreMaterials.route) }
        )
    }

    // Store Info
    composable("StoreInfo") {
        StoreInfoScreen(onBack = { navController.popBackStack() })
    }

    // Sellers
    composable("SellerList") {
        SellerListScreen(
            onAddSellerClick = { navController.navigate("AddSeller") },
            onBack = { navController.popBackStack() }
        )
    }
    composable("AddSeller") {
        AddSellerScreen(
            onSaved = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }

    // Products
    composable("ProductList") {
        ProductListScreen(
            onAddProductClick = { navController.navigate("AddProduct") },
            onBack = { navController.popBackStack() }
        )
    }
    composable("AddProduct") {
        AddProductScreen(
            onSaved = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }

    // Materials
    composable(StoreScreen.StoreMaterials.route) {
        MaterialListScreen(
            onAddMaterialClick = { navController.navigate("AddMaterial") },
            onBack = { navController.popBackStack() }
        )
    }
    composable("AddMaterial") {
        AddMaterialScreen(
            onSaved = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }

    // Sales
    composable(StoreScreen.StoreSales.route) {
        SalesScreen(onCreateSaleClick = { navController.navigate("CreateSale") })
    }
    composable("CreateSale") {
        PlaceholderScreen("ثبت فروش جدید")
    }
    composable(StoreScreen.StorePreOrders.route) {
        PlaceholderScreen("سفارشات")
    }
    composable(StoreScreen.StoreSaleInvoices.route) {
        PlaceholderScreen("فاکتورهای فروش")
    }
    composable(StoreScreen.StorePreOrderInvoices.route) {
        PlaceholderScreen("فاکتورهای پیش‌سفارش")
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "صفحه $title")
    }
}

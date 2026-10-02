package ir.siamak.fintrack.store.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Bottom navigation for the five primary store destinations. */
@Composable
fun StoreBottomBar(currentRoute: String?, onItemClick: (StoreScreen) -> Unit) {
    val items = listOf(
        Triple(StoreScreen.StoreDashboard, "داشبورد", Icons.Outlined.Home),
        Triple(StoreScreen.StoreBaseInfo, "اطلاعات پایه", Icons.Outlined.Storefront),
        Triple(StoreScreen.StoreSales, "فروش", Icons.Outlined.ReceiptLong),
        Triple(StoreScreen.StoreMaterials, "مواد اولیه", Icons.Outlined.Inventory2),
        Triple(StoreScreen.StorePreOrders, "سفارشات", Icons.Outlined.ShoppingBag)
    )
    NavigationBar { items.forEach { (screen, label, icon) -> NavigationBarItem(selected = currentRoute == screen.route, onClick = { onItemClick(screen) }, icon = { Icon(icon, label) }, label = { Text(label) }) } }
}

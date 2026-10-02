package ir.siamak.fintrack.storeaccountant.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * مدل نمایشی آیتم‌های نوار ناوبری پایین در بخش فروشگاه.
 */
data class StoreBottomNavItem(
    val screen: StoreScreen,
    val label: String,
    val icon: ImageVector
)

/**
 * نوار ناوبری پایین مخصوص ماژول حسابدار فروشگاه.
 */
@Composable
fun StoreBottomBar(
    currentRoute: String?,
    onItemClick: (StoreScreen) -> Unit
) {
    val items = listOf(
        StoreBottomNavItem(
            screen = StoreScreen.StoreDashboard,
            label = "داشبورد",
            icon = Icons.Default.Home
        ),
        StoreBottomNavItem(
            screen = StoreScreen.StoreBaseInfo,
            label = "اطلاعات پایه",
            icon = Icons.Default.Info
        ),
        StoreBottomNavItem(
            screen = StoreScreen.StoreSales,
            label = "فروش",
            icon = Icons.Default.ShoppingCart
        ),
        StoreBottomNavItem(
            screen = StoreScreen.StoreMaterials,
            label = "مواد اولیه",
            icon = Icons.Default.Inventory
        ),
        StoreBottomNavItem(
            screen = StoreScreen.StorePreOrders,
            label = "سفارشات",
            icon = Icons.Default.ListAlt
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = { onItemClick(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}


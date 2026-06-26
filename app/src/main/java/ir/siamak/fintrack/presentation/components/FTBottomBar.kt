package ir.siamak.fintrack.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ir.siamak.fintrack.presentation.navigation.Screen

/**
 * مدل نمایشی هر آیتم در bottom navigation.
 *
 * این مدل برای نگهداری اطلاعات لازم جهت نمایش هر تب اصلی استفاده می‌شود:
 * - route مقصد
 * - عنوان نمایشی
 * - آیکن
 *
 * @property screen صفحه‌ای که با کلیک روی آیتم باید باز شود
 * @property label عنوانی که در bottom bar نمایش داده می‌شود
 * @property icon آیکن نمایشی آیتم
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

/**
 * نوار ناوبری پایین (Bottom Navigation Bar) اپلیکیشن.
 *
 * این کامپوننت تب‌های اصلی برنامه را نمایش می‌دهد و مسئولیتش فقط نمایش UI و
 * ارسال رویداد کلیک به بیرون است. منطق navigation واقعی داخل NavGraph مدیریت می‌شود.
 *
 * مزیت این جداسازی:
 * - NavGraph تمیزتر می‌شود
 * - Bottom bar قابل استفاده مجدد می‌شود
 * - توسعه و نگهداری مسیرهای اصلی ساده‌تر خواهد شد
 *
 * @param currentScreen صفحه فعلی فعال برای مشخص کردن آیتم انتخاب‌شده
 * @param onItemClick callback کلیک روی هر آیتم
 */
@Composable
fun FTBottomBar(
    currentScreen: Screen?,
    onItemClick: (Screen) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            screen = Screen.Dashboard,
            label = "داشبورد",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            screen = Screen.WalletList,
            label = "حساب‌ها",
            icon = Icons.Default.CreditCard
        ),
        BottomNavItem(
            screen = Screen.Members,
            label = "اعضا",
            icon = Icons.Default.Group
        ),
        BottomNavItem(
            screen = Screen.Installments,
            label = "اقساط",
            icon = Icons.Default.ReceiptLong
        ),
        BottomNavItem(
            screen = Screen.Reports,
            label = "گزارشات",
            icon = Icons.Default.Assessment
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.screen,
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

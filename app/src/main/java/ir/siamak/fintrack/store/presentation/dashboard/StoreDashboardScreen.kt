package ir.siamak.fintrack.store.presentation.dashboard

import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.presentation.components.StoreContent

/** Store dashboard entry screen. */
@Composable fun StoreDashboardScreen(onSales: () -> Unit, onPreOrders: () -> Unit) = StoreContent("داشبورد فروشگاه", "فروش امروز، تعداد فاکتورها و آخرین فروش‌ها از داده‌های فروشگاه نمایش داده می‌شوند.", "ثبت فروش جدید", onSales)

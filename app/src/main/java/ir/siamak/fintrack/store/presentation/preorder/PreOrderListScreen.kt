package ir.siamak.fintrack.store.presentation.preorder

import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.presentation.components.StoreContent

/** Overview of custom pre-orders and their production status. */
@Composable fun PreOrderListScreen(onInvoices: () -> Unit) = StoreContent("سفارشات", "پیش‌سفارش‌های جدید، در حال ساخت و آماده تحویل", "فاکتورهای سفارش", onInvoices)

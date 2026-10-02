package ir.siamak.fintrack.store.presentation.sales.list

import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.presentation.components.StoreContent

/** Sales overview screen. */
@Composable fun SalesScreen(onInvoices: () -> Unit) = StoreContent("فروش", "فروش امروز، تعداد فاکتورها و آخرین فروش‌ها", "مشاهده فاکتورها", onInvoices)

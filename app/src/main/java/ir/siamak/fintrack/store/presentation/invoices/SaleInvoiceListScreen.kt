package ir.siamak.fintrack.store.presentation.invoices
import androidx.compose.runtime.Composable
@Composable fun SaleInvoiceListScreen(onDetail: (Long) -> Unit = {}) = InvoiceListScreen(false, onDetail)

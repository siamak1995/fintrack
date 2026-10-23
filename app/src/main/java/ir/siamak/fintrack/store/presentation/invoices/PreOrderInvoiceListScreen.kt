package ir.siamak.fintrack.store.presentation.invoices
import androidx.compose.runtime.Composable
@Composable fun PreOrderInvoiceListScreen(onDetail: (Long) -> Unit = {}) = InvoiceListScreen(true, onDetail)

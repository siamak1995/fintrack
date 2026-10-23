package ir.siamak.fintrack.store.presentation.invoices.sale
import androidx.compose.runtime.Composable
@Composable fun SaleInvoiceListScreen(onDetail: (Long) -> Unit) =
    ir.siamak.fintrack.store.presentation.invoices.InvoiceListScreen(false,onDetail)

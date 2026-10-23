package ir.siamak.fintrack.store.presentation.invoices.preorder
import androidx.compose.runtime.Composable
@Composable fun PreOrderInvoiceListScreen(onDetail: (Long) -> Unit) =
    ir.siamak.fintrack.store.presentation.invoices.InvoiceListScreen(true,onDetail)

package ir.siamak.fintrack.store.presentation.preorder.list
import androidx.compose.runtime.Composable
/** Canonical list route; retains the pre-existing presentation entry point for compatibility. */
@Composable fun PreOrderListScreen(onInvoices: () -> Unit = {}, onCreate: () -> Unit = {}) =
    ir.siamak.fintrack.store.presentation.preorder.PreOrderListScreen(onInvoices,onCreate)

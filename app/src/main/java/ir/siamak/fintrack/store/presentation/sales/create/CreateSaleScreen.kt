package ir.siamak.fintrack.store.presentation.sales.create
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.invoice.SaleInvoiceScreen
import ir.siamak.fintrack.store.presentation.sales.payment.SalePaymentScreen
import ir.siamak.fintrack.store.presentation.sales.discount.SaleDiscountScreen
import ir.siamak.fintrack.store.presentation.sales.products.SaleProductsScreen
import ir.siamak.fintrack.store.presentation.sales.customer.SaleCustomerScreen

@Composable fun CreateSaleScreen(onComplete: (Long) -> Unit = {}, vm: CreateSaleViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.invoiceId) { state.invoiceId?.let(onComplete) }
    CreateSaleContent(state,vm::onEvent)
}

@Composable fun CreateSaleContent(state: CreateSaleUiState, onEvent: (CreateSaleEvent) -> Unit) {
    val draft = state.draft
    StorePage("ثبت فروش — مرحله " + (draft.step + 1) + " از ۵", state.loading, state.error) {
        when(draft.step) {
            0 -> { SaleCustomerScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
            1 -> { SaleProductsScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
            2 -> { SaleDiscountScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
            3 -> { SalePaymentScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
            4 -> { SaleInvoiceScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
        }
        state.preview?.let { sale ->
            HorizontalDivider()
            Text("جمع کالاها: " + money(sale.subtotal))
            Text("تخفیف: " + money(sale.discountAmount) + " | مالیات: " + money(sale.taxAmount))
            Text("مبلغ نهایی با سود اقساط: " + money(sale.finalAmount), style = MaterialTheme.typography.titleMedium)
            sale.payment.installments.forEach { Text("قسط " + it.monthNumber + " — " + dateLabel(it.dueDate) + " — " + money(it.amount)) }
        }
        if(draft.step == 4) Button(enabled = !state.busy && state.preview != null, onClick = { onEvent(CreateSaleEvent.Submit) }) { Text(if(state.busy) "در حال ثبت…" else "تأیید و صدور فاکتور") }
        else Button(enabled = !state.loading && !state.busy, onClick = { onEvent(CreateSaleEvent.Next) }) { Text("مرحله بعد") }
        if(draft.step > 0) TextButton(enabled = !state.busy, onClick = { onEvent(CreateSaleEvent.Back) }) { Text("مرحله قبل") }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun CreateSalePreview() { MaterialTheme { CreateSaleContent(CreateSaleUiState(loading = false),{}) } }

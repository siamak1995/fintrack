package ir.siamak.fintrack.store.presentation.preorder.create
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.invoice.PreOrderInvoiceScreen
import ir.siamak.fintrack.store.presentation.preorder.payment.PreOrderPaymentScreen
import ir.siamak.fintrack.store.presentation.preorder.pricing.PreOrderPricingScreen
import ir.siamak.fintrack.store.presentation.preorder.materials.PreOrderMaterialScreen
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDescriptionScreen
import ir.siamak.fintrack.store.presentation.preorder.customer.PreOrderCustomerScreen

@Composable fun CreatePreOrderScreen(onComplete: (Long) -> Unit = {}, vm: CreatePreOrderViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.invoiceId) { state.invoiceId?.let(onComplete) }
    CreatePreOrderContent(state,vm::onEvent)
}

@Composable fun CreatePreOrderContent(state: CreatePreOrderUiState, onEvent: (CreatePreOrderEvent) -> Unit) {
    val draft = state.draft
    StorePage("سفارش اختصاصی — مرحله " + (draft.step + 1) + " از ۶", state.loading, state.error) {
        when(draft.step) {
            0 -> { PreOrderCustomerScreen(draft, state.workspace, state.preview, state.suggested, { change -> onEvent(CreatePreOrderEvent.Edit(change)) }) }
            1 -> { PreOrderDescriptionScreen(draft, state.workspace, state.preview, state.suggested, { change -> onEvent(CreatePreOrderEvent.Edit(change)) }) }
            2 -> { PreOrderMaterialScreen(draft, state.workspace, state.preview, state.suggested, { change -> onEvent(CreatePreOrderEvent.Edit(change)) }) }
            3 -> { PreOrderPricingScreen(draft, state.workspace, state.preview, state.suggested, state.materialCost, { change -> onEvent(CreatePreOrderEvent.Edit(change)) }) }
            4 -> { PreOrderPaymentScreen(draft, state.workspace, state.preview, state.suggested, { change -> onEvent(CreatePreOrderEvent.Edit(change)) }) }
            5 -> { PreOrderInvoiceScreen(draft, state.workspace, state.preview, state.suggested, { change -> onEvent(CreatePreOrderEvent.Edit(change)) }) }
        }
        state.preview?.let { order ->
            HorizontalDivider()
            Text("قیمت توافقی: " + money(order.agreedPrice))
            Text("پیش‌پرداخت: " + money(order.advancePayment))
            Text("مانده: " + money(ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoiceBalanceUseCase()(order)))
        }
        if(draft.step == 5) Button(enabled = !state.busy && state.preview != null, onClick = { onEvent(CreatePreOrderEvent.Submit) }) { Text(if(state.busy) "در حال ثبت…" else "ثبت سفارش و فاکتور") }
        else Button(enabled = !state.loading && !state.busy, onClick = { onEvent(CreatePreOrderEvent.Next) }) { Text("مرحله بعد") }
        if(draft.step > 0) TextButton(enabled = !state.busy, onClick = { onEvent(CreatePreOrderEvent.Back) }) { Text("مرحله قبل") }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun CreatePreOrderPreview() { MaterialTheme { CreatePreOrderContent(CreatePreOrderUiState(loading = false),{}) } }

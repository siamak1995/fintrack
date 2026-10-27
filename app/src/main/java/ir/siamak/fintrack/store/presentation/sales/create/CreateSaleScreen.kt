package ir.siamak.fintrack.store.presentation.sales.create
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    CreateSaleContent(state,vm::addProduct,vm::onEvent)
}

@Composable fun CreateSaleContent(state: CreateSaleUiState, onAddProduct: (suspend (ir.siamak.fintrack.store.domain.model.Product) -> Result<Unit>)? = null, onEvent: (CreateSaleEvent) -> Unit) {
    val draft = state.draft
    var quick by rememberSaveable { mutableStateOf(true) }
    var paymentOptions by rememberSaveable { mutableStateOf(draft.installments || draft.discount != "0" || draft.tax != "0") }
    val stage = when(draft.step) { 0 -> 0; 1 -> 1; 2,3 -> 2; else -> 3 }
    StorePage("ثبت فروش", state.loading, state.error, bottomBar = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            (state.error ?: state.validationMessage?.takeIf { (quick || draft.step == 4) && state.preview == null && !state.loading })?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            }
            if(state.preview != null && draft.settlement == null) Text("حساب دریافت وجه و شخص دریافت‌کننده را انتخاب کنید.",color = MaterialTheme.colorScheme.error,style = MaterialTheme.typography.labelMedium)
            state.preview?.let { DataRow("قابل پرداخت", money(it.finalAmount), true) }
            if(quick || draft.step == 4) Button(modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = !state.busy && state.preview != null && draft.settlement != null, onClick = { onEvent(CreateSaleEvent.Submit) }) { Text(if(state.busy) "در حال ثبت…" else "تأیید و صدور فاکتور") }
            else Button(modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = !state.loading && !state.busy, onClick = { onEvent(CreateSaleEvent.Next) }) { Text(if(stage == 2) "بازبینی فاکتور" else "ادامه") }
            if(!quick && draft.step > 0) FilledTonalButton(modifier = Modifier.fillMaxWidth(), enabled = !state.busy, onClick = { onEvent(CreateSaleEvent.Back) }) { Text("مرحله قبل") }
        }
    }) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(quick, { quick = true }, { Text("ثبت سریع") })
            FilterChip(!quick, { quick = false }, { Text("مرحله‌ای") })
        }
        if(quick) {
            SaleCustomerScreen(draft,state.workspace,state.preview) { change -> onEvent(CreateSaleEvent.Edit(change)) }
            SaleProductsScreen(draft,state.workspace,state.preview,{ change -> onEvent(CreateSaleEvent.Edit(change)) },onAddProduct)
            FilledTonalButton(modifier = Modifier.fillMaxWidth(), onClick = { paymentOptions = !paymentOptions }) { Text(if(paymentOptions) "بستن تنظیمات پرداخت" else "تخفیف، مالیات و پرداخت اقساطی") }
            if(paymentOptions) FormSection("تخفیف و پرداخت") {
                SaleDiscountScreen(draft,state.workspace,state.preview) { change -> onEvent(CreateSaleEvent.Edit(change)) }
                SalePaymentScreen(draft,state.workspace,state.preview) { change -> onEvent(CreateSaleEvent.Edit(change)) }
            }
        } else {
        WizardProgress(listOf("مشتری و فروشنده", "انتخاب کالا", "تخفیف و پرداخت", "تأیید فاکتور"), stage)
        when(draft.step) {
            0 -> { SaleCustomerScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
            1 -> { SaleProductsScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) },onAddProduct) }
            2,3 -> {
                FormSection("تخفیف و مالیات") { SaleDiscountScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
                FormSection("روش پرداخت") { SalePaymentScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
            }
            4 -> { SaleInvoiceScreen(draft, state.workspace, state.preview, { change -> onEvent(CreateSaleEvent.Edit(change)) }) }
        }
        }
        FormSection("واریز به حساب شخصی") {
            val finance = state.workspace.finance
            val account = draft.settlement
            val wallet = finance.wallets.firstOrNull { it.id == account?.walletId }
            var chosenWallet by rememberSaveable { mutableStateOf(account?.walletId) }
            LaunchedEffect(account?.walletId) { account?.walletId?.let { chosenWallet = it } }
            ChoiceEntry("حساب دریافت وجه *", finance.wallets.firstOrNull { it.id == chosenWallet }?.name ?: "انتخاب حساب یا پول نقد",
                finance.wallets.map { it.id to it.name }) { id ->
                chosenWallet = id
                val selected = finance.wallets.first { it.id == id }
                val member = finance.members.singleOrNull { it.contextId == selected.contextId }
                onEvent(CreateSaleEvent.Edit { it.copy(settlement = member?.let { ir.siamak.fintrack.store.domain.model.StoreSettlementAccount(selected.contextId,id,it.id) }) })
            }
            val selected = finance.wallets.firstOrNull { it.id == chosenWallet }
            if(selected != null) ChoiceEntry("شخص دریافت‌کننده *", finance.members.firstOrNull { it.id == account?.memberId }?.name ?: "انتخاب شخص",
                finance.members.filter { it.contextId == selected.contextId }.map { it.id to it.name }) { id ->
                onEvent(CreateSaleEvent.Edit { it.copy(settlement = ir.siamak.fintrack.store.domain.model.StoreSettlementAccount(selected.contextId,selected.id,id)) })
            }
            if(finance.members.isEmpty()) Text("ابتدا یک شخص در اطلاعات پایه حسابدار شخصی ثبت کنید.", color = MaterialTheme.colorScheme.error)
            if(wallet != null) Text("دریافت این فروش در حساب «" + wallet.name + "» ثبت می‌شود.", style = MaterialTheme.typography.bodySmall)
        }
        state.preview?.let { sale ->
            FormSection("خلاصه پرداخت") {
                DataRow("جمع کالاها", money(sale.subtotal))
                if(sale.discountAmount > 0) DataRow("تخفیف", money(sale.discountAmount))
                if(sale.taxAmount > 0) DataRow("مالیات", money(sale.taxAmount))
                HorizontalDivider()
                DataRow("قابل پرداخت", money(sale.finalAmount), true)
                sale.payment.installments.forEach { DataRow("قسط " + it.monthNumber + " · " + dateLabel(it.dueDate), money(it.amount)) }
            }
        }

    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun CreateSalePreview() { MaterialTheme { CreateSaleContent(CreateSaleUiState(loading = false),onEvent = {}) } }

package ir.siamak.fintrack.store.presentation.preorder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.presentation.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.domain.model.PreOrderStatus
import java.time.LocalDate
import java.time.temporal.ChronoUnit
fun statusLabel(status: PreOrderStatus): String = when(status) {
    PreOrderStatus.NEW -> "جدید"; PreOrderStatus.IN_PROGRESS -> "در حال تولید"; PreOrderStatus.READY -> "آماده"
    PreOrderStatus.DELIVERED -> "تحویل‌شده"; PreOrderStatus.CANCELLED -> "لغوشده"
}
fun remainingDays(value: String): String = runCatching {
    val days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(value))
    if(days < 0) (-days).toString() + " روز گذشته" else if(days == 0L) "امروز" else days.toString() + " روز دیگر"
}.getOrDefault(value)
@Composable fun PreOrderListScreen(onInvoices: () -> Unit = {}, onCreate: () -> Unit = {}, vm: StoreFeatureViewModel = hiltViewModel<PreOrderListViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var pending by remember { mutableStateOf<Pair<Long,PreOrderStatus>?>(null) }
    StorePage("سفارش‌های اختصاصی", state.loading, state.error, vm::reload) {
        ActionPair("ثبت سفارش", "فاکتورهای سفارش", onCreate,onInvoices)
        if(state.data.orders.isEmpty()) Text("هنوز سفارشی ثبت نشده است.")
        state.data.orders.forEach { order ->
            OutlinedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val document = state.data.invoices.firstOrNull { it.preOrderId == order.id }
                Text("نام مشتری: " + document?.customer?.name.orEmpty(), style = MaterialTheme.typography.titleMedium)
                DataRow("وضعیت",statusLabel(order.status))
                Text(order.description)
                DataRow("تحویل",dateLabel(order.deliveryDate) + " · " + remainingDays(order.deliveryDate))
                if(document != null) DataRow("کد رهگیری",ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(document))
                HorizontalDivider()
                DataRow("مبلغ توافقی",money(order.agreedPrice),true)
                DataRow("مانده",money(ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoiceBalanceUseCase()(order)))
                val next = when(order.status) {
                    PreOrderStatus.NEW -> PreOrderStatus.IN_PROGRESS
                    PreOrderStatus.IN_PROGRESS -> PreOrderStatus.READY
                    PreOrderStatus.READY -> PreOrderStatus.DELIVERED
                    else -> null
                }
                next?.let { Button(modifier = Modifier.fillMaxWidth(), enabled = !state.busy, onClick = { pending = order.id to it }) { Text(statusLabel(it)) } }
                if(next != null) FilledTonalButton(modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer,contentColor = MaterialTheme.colorScheme.onErrorContainer), enabled = !state.busy, onClick = { pending = order.id to PreOrderStatus.CANCELLED }) { Text("لغو سفارش") }
            } }
        }
    }
    pending?.let { (id,status) ->
        AlertDialog(onDismissRequest = { pending = null }, title = { Text("تغییر وضعیت سفارش") },
            text = { Text("وضعیت به «" + statusLabel(status) + "» تغییر کند؟ در صورت لغو، موجودی رزروشده آزاد می‌شود. سابقه دریافت وجه محفوظ می‌ماند؛ استرداد وجه باید جداگانه پیگیری شود.") },
            confirmButton = { TextButton(onClick = { vm.status(id,status); pending = null }) { Text("تأیید") } },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("انصراف") } })
    }
}

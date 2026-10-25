package ir.siamak.fintrack.store.presentation.invoices
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.model.Invoice.PaymentStatus
import ir.siamak.fintrack.store.presentation.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.remainingDays
import ir.siamak.fintrack.store.presentation.preorder.statusLabel
import ir.siamak.fintrack.store.core.pdf.InvoicePdfGenerator
import kotlinx.coroutines.*
import java.io.File

@Composable fun SaleInvoiceDetailScreen(invoiceId: Long, vm: StoreFeatureViewModel = hiltViewModel<SaleInvoiceDetailViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val invoice = state.data.invoices.firstOrNull { it.id == invoiceId }
    var receiving by rememberSaveable { mutableStateOf(false) }
    var amount by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }
    StorePage("جزئیات فاکتور", state.loading, localError ?: state.error, vm::reload) {
        if(invoice == null && !state.loading) Text("فاکتور یافت نشد.")
        invoice?.let {
            FormSection(it.store.name) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Picture(it.store.logoPath) }
                if(it.store.brand.isNotBlank()) DataRow("برند",it.store.brand)
                DataRow("نام مشتری",it.customer.name)
                DataRow("تلفن",it.customer.phone)
                it.customer.address?.let { address -> DataRow("آدرس",address) }
                DataRow("تاریخ",dateLabel(it.date))
                androidx.compose.foundation.text.selection.SelectionContainer { DataRow("کد رهگیری",ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(it)) }
                it.seller?.let { seller -> DataRow("فروشنده",seller.fullName) }
                DataRow("وضعیت",if(it.cancelled) "لغوشده" else paymentLabel(it.paymentStatus))
            }
            if(it.items.isNotEmpty()) ItemTable(it.items)
            if(it.description != null || it.materialItems.isNotEmpty() || it.deliveryDate != null) FormSection("جزئیات سفارش") {
                it.description?.let { description -> Text(description) }
                it.deliveryDate?.let { date -> DataRow("تحویل",dateLabel(date) + " · " + remainingDays(date)) }
                it.materialItems.forEach { item -> DataRow(item.materialName, quantity(item.quantity)) }
            }
            it.pricing?.let { pricing -> FormSection("برآورد قیمت") {
                DataRow("هزینه مواد",money(pricing.materialCost)); DataRow("تولید",money(pricing.productionCost))
                DataRow("سود",money(pricing.profit)); DataRow("ارسال",money(pricing.shippingCost)); DataRow("قیمت پیشنهادی",money(pricing.suggestedPrice))
            } }
            FormSection("خلاصه مالی") {
                DataRow("تخفیف",money(it.discountAmount)); DataRow("مالیات",money(it.taxAmount)); HorizontalDivider()
                DataRow("مبلغ کل",money(it.finalAmount),true)
                DataRow("پرداخت‌شده",money(it.payment?.paidAmount ?: 0))
                DataRow("مانده",money(ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoiceBalanceUseCase()(it)),true)
                if(it.cancelled) Text("استرداد دریافت‌های قبلی جداگانه پیگیری شود.",color = MaterialTheme.colorScheme.error)
            }
            if(it.payment?.installments.orEmpty().isNotEmpty()) FormSection("برنامه اقساط") {
                it.payment?.installments?.forEach { installment -> DataRow("قسط " + installment.monthNumber + " · " + dateLabel(installment.dueDate),money(installment.amount)) }
            }
            FormSection("ثبت در کیف پول شخصی") {
                val posted = state.data.finance.posted[it.invoiceNumber] ?: 0L
                DataRow("دریافتی ثبت‌شده", money(posted))
                if(it.invoiceNumber in state.data.finance.conflicts) Text("سابقهٔ کیف پول ویرایش شده و نیازمند بررسی است.",color = MaterialTheme.colorScheme.error)
                val paid = it.payment?.paidAmount ?: 0L
                val destination = it.settlement ?: state.data.stores.firstOrNull { store -> store.id == it.store.id }?.settlement
                if(it.settlement != null) Text(state.data.finance.wallets.firstOrNull { wallet -> wallet.id == it.settlement.walletId }?.name ?: "کیف پول مقصد در دسترس نیست")
                if(posted < paid && destination != null) {
                    Text("در انتظار ثبت: " + money(paid-posted),color = MaterialTheme.colorScheme.error)
                    FilledTonalButton(enabled = !state.busy, modifier = Modifier.fillMaxWidth(), onClick = { vm.syncInvoice(invoiceId) }) { Text("ثبت / تلاش مجدد در کیف پول") }
                } else if(destination == null) Text("کیف پول و شخص دریافت‌کننده را در اطلاعات فروشگاه انتخاب کنید.")
            }
            val cancelled = state.data.orders.firstOrNull { order -> order.id == it.preOrderId }?.status == PreOrderStatus.CANCELLED
            if(it.paymentStatus != PaymentStatus.SETTLED && !cancelled && !it.cancelled) Button(enabled = !state.busy, onClick = { receiving = true }) { Text("ثبت دریافت وجه") }
            FormSection("خروجی فاکتور") { InvoiceActions(it, state.data.invoices, state.data.sellers) }
        }
    }
    if(receiving && invoice != null) AlertDialog(onDismissRequest = { if(!state.busy) receiving = false },
        title = { Text("دریافت وجه (تومان)") },
        text = { Column { Entry("مبلغ", amount, { amount = it }, true); state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } } },
        confirmButton = { TextButton(enabled = !state.busy, onClick = {
            try { vm.receive(invoiceId, whole(amount)) { receiving = false; amount = "" } }
            catch(e: Exception) { localError = e.message; receiving = false }
        }) { Text("ثبت") } },
        dismissButton = { TextButton(enabled = !state.busy, onClick = { receiving = false }) { Text("انصراف") } })
}

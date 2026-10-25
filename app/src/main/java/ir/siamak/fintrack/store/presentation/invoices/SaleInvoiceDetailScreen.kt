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
            Picture(it.store.logoPath)
            Text(it.store.name + " — " + it.store.brand)
            Text("کد رهگیری", style = MaterialTheme.typography.labelLarge)
            androidx.compose.foundation.text.selection.SelectionContainer {
                Text(ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(it), style = MaterialTheme.typography.titleMedium)
            }
            Text(dateLabel(it.date))
            Text("مشتری: " + it.customer.name + " — " + it.customer.phone)
            it.customer.address?.let { address -> Text(address) }
            it.seller?.let { seller -> Text("فروشنده: " + seller.fullName); Picture(seller.signaturePath) }
            it.description?.let { description -> Text(description) }
            it.deliveryDate?.let { date -> Text("تحویل: " + dateLabel(date) + " — " + remainingDays(date)) }
            it.items.forEach { item -> Text(item.productName + " × " + item.quantity + " — " + money(item.unitPrice)) }
            it.materialItems.forEach { item -> Text(item.materialName + " — مقدار مصرف: " + item.quantity) }

            it.pricing?.let { pricing ->
                Text("هزینه مواد: " + money(pricing.materialCost))
                Text("تولید: " + money(pricing.productionCost) + " | سود: " + money(pricing.profit) + " | ارسال: " + money(pricing.shippingCost))
                Text("قیمت پیشنهادی: " + money(pricing.suggestedPrice))
            }
            HorizontalDivider()
            Text("تخفیف: " + money(it.discountAmount)); Text("مالیات: " + money(it.taxAmount))
            Text("مبلغ کل: " + money(it.finalAmount), style = MaterialTheme.typography.titleLarge)
            Text("پرداخت‌شده: " + money(it.payment?.paidAmount ?: 0))
            Text("مانده: " + money(ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoiceBalanceUseCase()(it)))
            Text(if(it.cancelled) "لغوشده — استرداد دریافت‌های قبلی را جداگانه پیگیری کنید." else paymentLabel(it.paymentStatus))
            it.payment?.installments?.forEach { installment -> Text("قسط " + installment.monthNumber + " — " + dateLabel(installment.dueDate) + " — " + money(installment.amount)) }
            val cancelled = state.data.orders.firstOrNull { order -> order.id == it.preOrderId }?.status == PreOrderStatus.CANCELLED
            if(it.paymentStatus != PaymentStatus.SETTLED && !cancelled && !it.cancelled) Button(enabled = !state.busy, onClick = { receiving = true }) { Text("ثبت دریافت وجه") }
            InvoiceActions(it, state.data.invoices)
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

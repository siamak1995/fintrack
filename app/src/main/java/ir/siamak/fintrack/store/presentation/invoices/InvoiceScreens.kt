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

fun paymentLabel(status: PaymentStatus) = when(status) { PaymentStatus.SETTLED -> "تسویه‌شده"; PaymentStatus.UNSETTLED -> "تسویه‌نشده"; PaymentStatus.INSTALLMENT -> "اقساطی" }

@Composable fun InvoiceListScreen(preorder: Boolean, onDetail: (Long) -> Unit, vm: StoreFeatureViewModel = hiltViewModel<InvoiceListViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var filter by rememberSaveable { mutableIntStateOf(0) }
    StorePage(if(preorder) "فاکتورهای پیش‌سفارش" else "فاکتورهای فروش", state.loading, state.error, vm::reload) {
        listOf("همه","تسویه‌شده","تسویه‌نشده","اقساطی").forEachIndexed { index, label ->
            FilterChip(selected = filter == index, onClick = { filter = index }, label = { Text(label) })
        }
        val records = state.data.invoices.filter { (it.preOrderId != null) == preorder }.filter { invoice ->
            when(filter) {
                1 -> invoice.paymentStatus == PaymentStatus.SETTLED
                2 -> invoice.paymentStatus != PaymentStatus.SETTLED
                3 -> invoice.payment?.type == Payment.PaymentType.INSTALLMENT
                else -> true
            }
        }
        if(records.isEmpty()) Text("فاکتوری با این فیلتر وجود ندارد.")
        records.forEach { invoice ->
            OutlinedCard(onClick = { onDetail(invoice.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(invoice.invoiceNumber, style = MaterialTheme.typography.titleMedium)
                    Text(invoice.customer.name + " — " + money(invoice.finalAmount))
                    Text(dateLabel(invoice.date) + " — " + if(invoice.cancelled) "لغوشده" else paymentLabel(invoice.paymentStatus))
                    invoice.deliveryDate?.let { Text("تحویل: " + dateLabel(it) + " — " + remainingDays(it)) }
                    state.data.orders.firstOrNull { it.id == invoice.preOrderId }?.let { Text(statusLabel(it.status)) }
                }
            }
        }
    }
}

@Composable internal fun InvoiceActions(invoice: Invoice) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var preview by remember { mutableStateOf<File?>(null) }
    suspend fun write(uri: Uri) = withContext(Dispatchers.IO) {
        val logo = invoice.store.logoPath?.let { path -> runCatching {
            context.contentResolver.openInputStream(Uri.parse(path))?.use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = 4 }) }
        }.getOrNull() }
        try { requireNotNull(context.contentResolver.openOutputStream(uri)).use { InvoicePdfGenerator().generate(invoice,it,logo) } }
        finally { logo?.recycle() }
    }
    fun action(block: suspend () -> Unit) {
        if(busy) return
        scope.launch {
            busy = true; message = null
            try { block() } catch(cancel: CancellationException) { throw cancel }
            catch(e: Exception) { message = "خروجی ناموفق: " + e.message }
            finally { busy = false }
        }
    }
    val save = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { action { write(it); message = "فایل PDF ذخیره شد." } }
    }
    suspend fun file(): File {
        val directory = File(context.cacheDir,"store-invoices").apply { mkdirs() }
        val target = File(directory,"invoice-" + invoice.id + ".pdf")
        write(Uri.fromFile(target))
        return target
    }
    Button(enabled = !busy, onClick = { action { preview = file() } }) { Text("پیش‌نمایش PDF") }
    OutlinedButton(enabled = !busy, onClick = { save.launch("invoice-" + invoice.id + ".pdf") }) { Text("دانلود / ذخیره PDF") }
    OutlinedButton(enabled = !busy, onClick = { action {
        val target = file()
        val uri = FileProvider.getUriForFile(context, context.packageName + ".store.files", target)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"; putExtra(Intent.EXTRA_STREAM,uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = android.content.ClipData.newRawUri("فاکتور",uri)
        }
        context.startActivity(Intent.createChooser(intent,"اشتراک‌گذاری فاکتور"))
    } }) { Text("اشتراک‌گذاری") }
    if(busy) LinearProgressIndicator()
    message?.let { Text(it) }
    preview?.let { PdfPreview(it) { preview = null } }
}
@Composable private fun PdfPreview(file: File, dismiss: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    var pages by remember { mutableIntStateOf(1) }
    var error by remember { mutableStateOf<String?>(null) }
    val bitmap by produceState<Bitmap?>(null, file, page) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                    PdfRenderer(descriptor).use { renderer ->
                        pages = renderer.pageCount
                        renderer.openPage(page).use { pdfPage ->
                            Bitmap.createBitmap(pdfPage.width * 2, pdfPage.height * 2, Bitmap.Config.ARGB_8888).also {
                                it.eraseColor(android.graphics.Color.WHITE)
                                pdfPage.render(it, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            }
                        }
                    }
                }
            }.onFailure { error = it.message }.getOrNull()
        }
    }
    AlertDialog(onDismissRequest = dismiss, title = { Text("صفحه " + (page + 1) + " از " + pages) },
        text = { Column {
            error?.let { Text(it) }
            bitmap?.let { Image(it.asImageBitmap(),"پیش‌نمایش فاکتور",Modifier.fillMaxWidth().heightIn(max = 450.dp)) }
            Row { TextButton(enabled = page > 0, onClick = { page-- }) { Text("قبلی") }; TextButton(enabled = page + 1 < pages, onClick = { page++ }) { Text("بعدی") } }
        } }, confirmButton = { TextButton(onClick = dismiss) { Text("بستن") } })
}

package ir.siamak.fintrack.store.presentation.invoices
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("همه","تسویه‌شده","تسویه‌نشده","اقساطی").forEachIndexed { index, label ->
            FilterChip(selected = filter == index, onClick = { filter = index }, label = { Text(label) })
        }
        }
        var search by rememberSaveable { mutableStateOf("") }
        Entry("جست‌وجوی مشتری، تلفن یا کد رهگیری", search, { search = it })
        val query = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(search.trim())
        val records = state.data.invoices.filter { (it.preOrderId != null) == preorder }.filter { invoice ->
            when(filter) {
                1 -> invoice.paymentStatus == PaymentStatus.SETTLED
                2 -> invoice.paymentStatus != PaymentStatus.SETTLED
                3 -> invoice.payment?.type == Payment.PaymentType.INSTALLMENT
                else -> true
            }
        }
        val visible = records.filter { invoice -> query.isBlank() || listOf(invoice.customer.name, invoice.customer.phone, ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(invoice)).any {
            ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(it).contains(query, true)
        } }
        if(visible.isEmpty()) Text("فاکتوری با این فیلتر وجود ندارد.")
        visible.forEach { invoice ->
            OutlinedCard(onClick = { onDetail(invoice.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("نام مشتری: " + invoice.customer.name, style = MaterialTheme.typography.titleMedium)
                    Text(money(invoice.finalAmount), style = MaterialTheme.typography.titleLarge)
                    Text(dateLabel(invoice.date) + " — " + if(invoice.cancelled) "لغوشده" else paymentLabel(invoice.paymentStatus))
                    Text("کد رهگیری: " + ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(invoice), style = MaterialTheme.typography.labelMedium)
                    invoice.deliveryDate?.let { Text("تحویل: " + dateLabel(it) + " — " + remainingDays(it)) }
                    state.data.orders.firstOrNull { it.id == invoice.preOrderId }?.let { Text(statusLabel(it.status)) }
                }
            }
        }
    }
}

@Composable internal fun InvoiceActions(invoice: Invoice, related: List<Invoice>? = null, sellers: List<ir.siamak.fintrack.store.domain.model.Seller> = emptyList(), brands: List<ir.siamak.fintrack.store.domain.model.Brand> = emptyList()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var preview by remember { mutableStateOf<File?>(null) }
    val candidates = sellers.filter { it.signaturePath != null && (invoice.seller == null || it.id == invoice.seller.id) }
    var signatureId by rememberSaveable(invoice.id) { mutableStateOf<String?>(null) }
    val resolvedSignatureId = signatureId ?: if(invoice.seller?.signaturePath != null) "snapshot" else invoice.seller?.id?.takeIf { id -> candidates.any { it.id == id } }?.toString() ?: "none"
    val signer = candidates.firstOrNull { it.id.toString() == resolvedSignatureId }
    val selectedSignature = if(resolvedSignatureId == "snapshot") invoice.seller?.signaturePath else signer?.signaturePath
    if(invoice.seller?.signaturePath != null || candidates.isNotEmpty()) {
        ChoiceEntry("امضای این خروجی", if(resolvedSignatureId == "snapshot") "امضای ثبت‌شده" else signer?.fullName ?: "بدون امضا", listOf("none" to "بدون امضا") + listOfNotNull(invoice.seller?.signaturePath?.let { "snapshot" to "امضای ثبت‌شده" }) + candidates.map { it.id.toString() to (it.fullName + " · فعلی") }) { signatureId = it }
    }
    if(selectedSignature != null) Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) { Picture(selectedSignature) }
    suspend fun write(uri: Uri) = withContext(Dispatchers.IO) {
        val brandPath = invoice.store.brandImagePath ?: brands.firstOrNull { (invoice.store.brandId != null && it.id == invoice.store.brandId) || (invoice.store.brandId == null && invoice.store.brand.isNotBlank() && it.name == invoice.store.brand) }?.image
        val branded = invoice.copy(store = invoice.store.copy(brandImagePath = brandPath))
        val printable = if(selectedSignature != null) branded.copy(seller = (invoice.seller ?: signer)?.copy(signaturePath = selectedSignature)) else branded.copy(seller = invoice.seller?.copy(signaturePath = null))
        fun decode(path: String): Bitmap? = runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(Uri.parse(path))?.use { BitmapFactory.decodeStream(it,null,bounds) }
            var sample = 1
            while(bounds.outWidth/sample > 1600 || bounds.outHeight/sample > 1600) sample *= 2
            context.contentResolver.openInputStream(Uri.parse(path))?.use { BitmapFactory.decodeStream(it,null,BitmapFactory.Options().apply { inSampleSize = sample }) }
        }.getOrNull()
        val logo = brandPath?.let(::decode)
        if(brandPath != null && logo == null) error("تصویر برند در دسترس نیست؛ تصویر برند را دوباره انتخاب کنید")
        val signature = selectedSignature?.let(::decode)
        if(selectedSignature != null && signature == null) { logo?.recycle(); error("تصویر امضا در دسترس نیست؛ تصویر فروشنده را دوباره انتخاب کنید") }
        val regular = androidx.core.content.res.ResourcesCompat.getFont(context,ir.siamak.fintrack.R.font.store_regular)
        val bold = androidx.core.content.res.ResourcesCompat.getFont(context,ir.siamak.fintrack.R.font.store_bold)
        try { requireNotNull(context.contentResolver.openOutputStream(uri)).use { InvoicePdfGenerator().generate(printable,it,logo,signature,related,regular,bold) } }
        finally { logo?.recycle(); signature?.recycle() }
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
        val target = File(directory, ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(invoice) + ".pdf")
        write(Uri.fromFile(target))
        return target
    }
    Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = { action { preview = file() } }) { Text("پیش‌نمایش PDF") }
    FilledTonalButton(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = { save.launch(ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(invoice) + ".pdf") }) { Text("دانلود / ذخیره PDF") }
    OutlinedButton(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = { action {
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

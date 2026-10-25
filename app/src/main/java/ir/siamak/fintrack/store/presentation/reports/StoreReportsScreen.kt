package ir.siamak.fintrack.store.presentation.reports

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.core.pdf.StoreReportPdfGenerator
import kotlinx.coroutines.*

@Composable
fun StoreReportsScreen(onDetail: (Long) -> Unit, onBack: () -> Unit, vm: StoreReportsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val report = state.report
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var exportSnapshot by remember { mutableStateOf<StoreReport?>(null) }
    val save = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val snapshot = exportSnapshot
        if(uri != null && snapshot != null) scope.launch {
            exporting = true
            try {
                withContext(Dispatchers.IO) { requireNotNull(context.contentResolver.openOutputStream(uri)).use { StoreReportPdfGenerator().generate(snapshot, it) } }
                exportMessage = "گزارش همهٔ نتایج فیلترشده ذخیره شد."
            } catch(cancel: CancellationException) { throw cancel }
            catch(error: Exception) { exportMessage = "ذخیرهٔ گزارش ناموفق بود: " + error.message }
            finally { exporting = false; exportSnapshot = null }
        }
    }
    StorePage("گزارش‌های فروشگاه", state.loading, state.error, vm::retry) {
        TextButton(onClick = onBack) { Text("بازگشت") }
        report?.let { result ->
            val filter = result.filter
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(filter.document == ReportDocument.SALE, { vm.change { it.copy(document = ReportDocument.SALE, productId = null) } }, { Text("فروش") })
                FilterChip(filter.document == ReportDocument.PREORDER, { vm.change { it.copy(document = ReportDocument.PREORDER, productId = null) } }, { Text("پیش‌سفارش") })
            }
            DateEntry("از تاریخ", filter.from.toString()) { value -> vm.change { it.copy(from = java.time.LocalDate.parse(value)) } }
            DateEntry("تا تاریخ", filter.to.toString()) { value -> vm.change { it.copy(to = java.time.LocalDate.parse(value)) } }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { vm.change { it.copy(from = java.time.LocalDate.now(), to = java.time.LocalDate.now()) } }) { Text("امروز") }
                TextButton(onClick = { vm.change { it.copy(from = java.time.LocalDate.now().minusDays(6), to = java.time.LocalDate.now()) } }) { Text("هفت روز اخیر") }
                TextButton(onClick = vm::reset) { Text("پاک‌کردن فیلترها") }
            }
            Entry("جست‌وجوی شماره، مشتری یا کالا", filter.search, { query -> vm.change { it.copy(search = query) } })
            ReportChoice("مشتری", filter.customerId?.let { id -> state.invoices.firstOrNull { it.customer.id == id }?.customer?.name } ?: "همه",
                listOf(null to "همه") + state.invoices.map { it.customer }.distinctBy { it.id }.map { it.id to (it.name + " — " + it.phone) }) { id -> vm.change { it.copy(customerId = id) } }
            if(filter.document == ReportDocument.SALE) ReportChoice("کالا", filter.productId?.let { id -> state.invoices.flatMap { it.items }.firstOrNull { it.productId == id }?.productName } ?: "همه",
                listOf(null to "همه") + state.invoices.flatMap { it.items }.distinctBy { it.productId }.map { it.productId to it.productName }) { id -> vm.change { it.copy(productId = id) } }
            ReportChoice("تسویه", settlement(filter.settlement), ReportSettlement.entries.map { it to settlement(it) }) { value -> vm.change { it.copy(settlement = value) } }
            ReportChoice("ترتیب", order(filter.order), ReportOrder.entries.map { it to order(it) }) { value -> vm.change { it.copy(order = value) } }
            Text("شاخص‌های فاکتورهای این بازه", style = MaterialTheme.typography.titleLarge)
            Summary(result.totals)
            Text("دریافتی و مانده، وضعیت فعلی فاکتورهای صادرشده در این بازه‌اند؛ گزارش گردش وجه در تاریخ دریافت نیستند.", style = MaterialTheme.typography.bodySmall)
            if(filter.productId != null) Text("فیلتر کالا، فاکتورهای حاوی آن کالا را انتخاب می‌کند. مبالغ بالا مربوط به کل همان فاکتورهاست؛ سهم تخفیف یا مالیات به کالا تخصیص داده نشده است.")
            if(result.excludedCancelled > 0) Text("فاکتور لغوشدهٔ خارج از جمع‌ها: " + fa(result.excludedCancelled))
            if(result.invalidDates > 0) Text("فاکتور دارای تاریخ ذخیره‌شدهٔ غیرقابل‌تفسیر: " + fa(result.invalidDates) + "؛ در جمع دوره لحاظ نشده و از فهرست فاکتورها قابل بررسی است.", color = MaterialTheme.colorScheme.error)
            Text("فاکتور دارای برنامهٔ سررسید گذشته و مانده: " + fa(result.totals.dueScheduleInvoices))
            Text("پرداخت هر قسط جداگانه ثبت نمی‌شود؛ این شمارش به معنی مبلغ دقیق معوق نیست.", style = MaterialTheme.typography.bodySmall)
            Text("مقایسهٔ دوره‌ها", style = MaterialTheme.typography.titleLarge)
            DateEntry("شروع دورهٔ مقایسه", result.comparisonFrom.toString()) { date -> vm.change { it.copy(comparisonFrom = java.time.LocalDate.parse(date), comparisonTo = result.comparisonTo) } }
            DateEntry("پایان دورهٔ مقایسه", result.comparisonTo.toString()) { date -> vm.change { it.copy(comparisonFrom = result.comparisonFrom, comparisonTo = java.time.LocalDate.parse(date)) } }
            Text("دورهٔ مقایسه با همین فیلتر مشتری/کالا/تسویه: " + money(result.comparison.payable) + " — " + fa(result.comparison.count) + " فاکتور")
            Text("اختلاف مبلغ قابل پرداخت: " + money(Math.subtractExact(result.totals.payable, result.comparison.payable)))
            ReportChoice("گروه‌بندی روند", period(filter.period), ReportPeriod.entries.map { it to period(it) }) { value -> vm.change { it.copy(period = value) } }
            Text("روند مبلغ قابل پرداخت — تومان", style = MaterialTheme.typography.titleLarge)
            if(result.trend.isEmpty()) Text("در این بازه داده‌ای برای نمودار وجود ندارد.")
            val max = result.trend.maxOfOrNull { it.amount }?.coerceAtLeast(1) ?: 1
            result.trend.forEach { bucket ->
                Text(PersianNumberFormatter.toPersianDigits(bucket.label) + " — " + money(bucket.amount) + " — " + fa(bucket.count) + " فاکتور")
                LinearProgressIndicator(progress = { (bucket.amount.toDouble() / max).toFloat().coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth())
            }
            if(result.trend.isNotEmpty()) Text("طول میله از صفر تا " + money(max) + " است؛ تاریخ‌ها شمسی و شروع هفته شنبه است.", style = MaterialTheme.typography.bodySmall)
            if(filter.document == ReportDocument.SALE) {
                Text("کالاها — مبلغ ناخالص پیش از تخفیف و مالیات", style = MaterialTheme.typography.titleMedium)
                result.products.forEach { item -> Text(item.name + " | " + fa(item.quantity) + " عدد | " + money(item.gross)) }
            }
            Text("مشتریان — جمع فاکتور و مانده", style = MaterialTheme.typography.titleMedium)
            result.customers.forEach { customer -> Text(customer.name + " | " + fa(customer.count) + " فاکتور | " + money(customer.payable) + " | مانده: " + money(customer.balance)) }
            Text("جزئیات فاکتورها", style = MaterialTheme.typography.titleLarge)
            if(result.rows.isEmpty()) Text("فاکتوری مطابق این فیلترها پیدا نشد.")
            result.rows.forEach { invoice ->
                OutlinedCard(onClick = { onDetail(invoice.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(PersianNumberFormatter.toPersianDigits(invoice.invoiceNumber), style = MaterialTheme.typography.titleSmall)
                        Text(invoice.customer.name + " — " + dateLabel(invoice.date))
                        Text("قابل پرداخت: " + money(invoice.finalAmount))
                        Text("دریافت: " + money(invoice.payment?.paidAmount ?: 0) + " — مانده: " + money(invoice.finalAmount - (invoice.payment?.paidAmount ?: 0)))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(enabled = filter.page > 0, onClick = { vm.page(filter.page - 1) }) { Text("قبلی") }
                Text("صفحهٔ " + fa(filter.page + 1) + " از " + fa(result.pages))
                TextButton(enabled = filter.page + 1 < result.pages, onClick = { vm.page(filter.page + 1) }) { Text("بعدی") }
            }
            Button(enabled = !exporting, onClick = { exportSnapshot = result; save.launch("store-report.pdf") }) { Text("ذخیرهٔ PDF گزارش کامل") }
            if(exporting) LinearProgressIndicator(Modifier.fillMaxWidth())
            exportMessage?.let { Text(it) }
            Text("تخفیف اقلام، گروه کالا، پرداخت‌های خروجی و تاریخ تک‌تک دریافت‌ها در مدل فعلی ثبت نمی‌شوند؛ گزارش ساختگی برای آن‌ها تولید نشده است.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable private fun Summary(t: StoreReportTotals) {
    OutlinedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("تعداد فاکتور: " + fa(t.count))
        Text("مبلغ قابل پرداخت: " + money(t.payable), style = MaterialTheme.typography.titleLarge)
        Text("دریافتی فعلی: " + money(t.received) + " — مانده: " + money(t.balance))
        Text("ناخالص کالا: " + money(t.grossItems))
        Text("تخفیف فاکتور: " + money(t.discount) + " — مالیات: " + money(t.tax))
        Text("افزودهٔ اقساط: " + money(t.financeCharge))
        if(t.unreconciledInvoices > 0) Text("فاکتور با اقلام ناقص یا اختلاف مبلغ: " + fa(t.unreconciledInvoices) +
            "؛ جمع قابل پرداخت از سند ذخیره‌شده است. اختلاف نقدی یا سند فاقد اقلام، سود اقساط شمرده نشده است.", color = MaterialTheme.colorScheme.error)
    } }
}
@Composable private fun <T> ReportChoice(title: String, selected: String, options: List<Pair<T,String>>, change: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(title + ": " + PersianNumberFormatter.toPersianDigits(selected)) }
        DropdownMenu(expanded, { expanded = false }) { options.forEach { (value, label) ->
            DropdownMenuItem(text = { Text(PersianNumberFormatter.toPersianDigits(label)) }, onClick = { expanded = false; change(value) })
        } }
    }
}
private fun fa(value: Number) = PersianNumberFormatter.toPersianDigits(value.toString())
private fun settlement(value: ReportSettlement) = when(value) { ReportSettlement.ALL -> "همه"; ReportSettlement.SETTLED -> "تسویه‌شده"; ReportSettlement.OUTSTANDING -> "دارای مانده"; ReportSettlement.INSTALLMENT -> "اقساطی" }
private fun period(value: ReportPeriod) = when(value) { ReportPeriod.DAY -> "روزانه"; ReportPeriod.WEEK -> "هفتگی"; ReportPeriod.MONTH -> "ماهانه"; ReportPeriod.YEAR -> "سالانه" }
private fun order(value: ReportOrder) = when(value) { ReportOrder.NEWEST -> "جدیدترین"; ReportOrder.OLDEST -> "قدیمی‌ترین"; ReportOrder.AMOUNT_DESC -> "بیشترین مبلغ"; ReportOrder.BALANCE_DESC -> "بیشترین مانده" }

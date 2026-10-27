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
import androidx.compose.runtime.saveable.rememberSaveable
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
    var basis by rememberSaveable { mutableStateOf<String?>(null) }
    var filtersOpen by rememberSaveable { mutableStateOf(true) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var section by rememberSaveable { mutableIntStateOf(0) }
    val save = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val snapshot = exportSnapshot
        if(uri != null && snapshot != null) scope.launch {
            exporting = true
            try {
                withContext(Dispatchers.IO) {
                    val regular = androidx.core.content.res.ResourcesCompat.getFont(context,ir.siamak.fintrack.R.font.store_regular)
                    val bold = androidx.core.content.res.ResourcesCompat.getFont(context,ir.siamak.fintrack.R.font.store_bold)
                    requireNotNull(context.contentResolver.openOutputStream(uri)).use { StoreReportPdfGenerator().generate(snapshot, it, regular, bold) }
                }
                exportMessage = "گزارش همهٔ نتایج فیلترشده ذخیره شد."
            } catch(cancel: CancellationException) { throw cancel }
            catch(error: Exception) { exportMessage = "ذخیرهٔ گزارش ناموفق بود: " + error.message }
            finally { exporting = false; exportSnapshot = null }
        }
    }
    StorePage("گزارش‌های فروشگاه", state.loading, state.error, vm::retry) {
        if(basis == null) {
            Text("گزارش را بر چه اساسی می‌خواهید؟", style = MaterialTheme.typography.titleLarge)
            listOf("day" to "گزارش یک روز", "store" to "فروشگاه / نمایشگاه", "seller" to "فروشنده", "customer" to "مشتری", "product" to "محصول", "settlement" to "وضعیت تسویه", "compare" to "مقایسه فروشگاه‌ها و روند فروش", "advanced" to "گزارش ترکیبی پیشرفته").forEach { (key,label) ->
                OutlinedCard(onClick = {
                    vm.reset(); basis = key; showResult = false; filtersOpen = true; section = if(key == "compare") 1 else 0
                    if(key == "day") vm.change { it.copy(from = java.time.LocalDate.now(), to = java.time.LocalDate.now()) }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(label, Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
        } else report?.let { result ->
            val filter = result.filter
            TextButton(onClick = { basis = null; showResult = false }) { Text("تغییر مبنای گزارش") }
            if(!filtersOpen) {
                DataRow("تاریخ", if(filter.from == filter.to) dateLabel(filter.from.toString()) else dateLabel(filter.from.toString()) + " تا " + dateLabel(filter.to.toString()))
                FilledTonalButton(onClick = { filtersOpen = true }, modifier = Modifier.fillMaxWidth()) { Text("ویرایش انتخاب‌ها") }
            }
            if(filtersOpen) FormSection("انتخاب گزارش") {
                val advanced = basis == "advanced"
                if(basis in listOf("store", "compare", "advanced")) ChoiceEntry("فروشگاه / نمایشگاه", state.stores.firstOrNull { it.id == filter.storeId }?.name ?: "همه فروشگاه‌ها",
                    listOf(null to "همه فروشگاه‌ها") + state.stores.map { it.id to it.name }, vm::selectStore)
                if(basis == "seller" || advanced) ChoiceEntry("فروشنده", state.invoices.mapNotNull { it.seller }.firstOrNull { it.id == filter.sellerId }?.fullName ?: "همه فروشندگان",
                    listOf(null to "همه فروشندگان") + state.invoices.mapNotNull { it.seller }.distinctBy { it.id }.map { it.id to it.fullName }) { id -> vm.change { it.copy(sellerId = id) } }
                if(basis == "customer" || advanced) ChoiceEntry("مشتری", state.invoices.firstOrNull { it.customer.id == filter.customerId }?.customer?.name ?: "همه مشتریان",
                    listOf(null to "همه مشتریان") + state.invoices.map { it.customer }.distinctBy { it.id }.map { it.id to (it.name + " · " + it.phone) }) { id -> vm.change { it.copy(customerId = id) } }
                if(basis == "product" || advanced) ChoiceEntry("محصول", state.invoices.flatMap { it.items }.firstOrNull { it.productId == filter.productId }?.productName ?: "همه محصولات",
                    listOf(null to "همه محصولات") + state.invoices.flatMap { it.items }.distinctBy { it.productId }.map { it.productId to it.productName }) { id -> vm.change { it.copy(productId = id) } }
                if(basis == "settlement" || advanced) ChoiceEntry("تسویه", settlement(filter.settlement), ReportSettlement.entries.map { it to settlement(it) }) { value -> vm.change { it.copy(settlement = value) } }
                if(basis != "product") Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(filter.document == ReportDocument.SALE, { vm.change { it.copy(document = ReportDocument.SALE, productId = null) } }, { Text("فروش") })
                    FilterChip(filter.document == ReportDocument.PREORDER, { vm.change { it.copy(document = ReportDocument.PREORDER, productId = null) } }, { Text("پیش‌سفارش") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(filter.from == filter.to, { vm.change { it.copy(from = it.to) } }, { Text("یک روز") })
                    FilterChip(filter.from != filter.to, { vm.change { it.copy(from = it.to.minusDays(6)) } }, { Text("بازه زمانی") })
                    TextButton(onClick = { vm.change { it.copy(from = java.time.LocalDate.now(), to = java.time.LocalDate.now()) } }) { Text("امروز") }
                }
                if(filter.from == filter.to) DateEntry("روز گزارش", filter.to.toString()) { date -> vm.change { it.copy(from = java.time.LocalDate.parse(date), to = java.time.LocalDate.parse(date)) } }
                else {
                    DateEntry("از تاریخ", filter.from.toString()) { date -> vm.change { it.copy(from = java.time.LocalDate.parse(date)) } }
                    DateEntry("تا تاریخ", filter.to.toString()) { date -> vm.change { it.copy(to = java.time.LocalDate.parse(date)) } }
                }
                if(basis == "compare" || advanced) ChoiceEntry("مقایسه با", state.stores.firstOrNull { it.id == filter.comparisonStoreId }?.name ?: "دوره قبل",
                    listOf(null to "دوره قبل") + state.stores.map { it.id to it.name }) { id -> vm.change { it.copy(comparisonStoreId = id, comparisonFrom = null, comparisonTo = null) } }
                if(advanced) {
                    Entry("جست‌وجوی شماره، مشتری یا کالا", filter.search, { query -> vm.change { it.copy(search = query) } })
                    ChoiceEntry("ترتیب", order(filter.order), ReportOrder.entries.map { it to order(it) }) { value -> vm.change { it.copy(order = value) } }
                }
                Button(onClick = { showResult = true; filtersOpen = false }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("نمایش گزارش") }
            }
            if(showResult && !filtersOpen) {
            Summary(result.totals)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("فاکتورها", "روند و مقایسه", "کالا و مشتری", "مبنای محاسبه").forEachIndexed { index, label ->
                    FilterChip(section == index, { section = index }, { Text(label) })
                }
            }
            if(section == 3) {
            Text("دریافتی و مانده، وضعیت فعلی فاکتورهای صادرشده در این بازه‌اند؛ گزارش گردش وجه در تاریخ دریافت نیستند.", style = MaterialTheme.typography.bodySmall)
            if(filter.productId != null) Text("فیلتر کالا، فاکتورهای حاوی آن کالا را انتخاب می‌کند. مبالغ بالا مربوط به کل همان فاکتورهاست؛ سهم تخفیف یا مالیات به کالا تخصیص داده نشده است.")
            if(result.excludedCancelled > 0) Text("فاکتور لغوشدهٔ خارج از جمع‌ها: " + fa(result.excludedCancelled))
            if(result.invalidDates > 0) Text("فاکتور دارای تاریخ ذخیره‌شدهٔ غیرقابل‌تفسیر: " + fa(result.invalidDates) + "؛ در جمع دوره لحاظ نشده و از فهرست فاکتورها قابل بررسی است.", color = MaterialTheme.colorScheme.error)
            Text("فاکتور دارای برنامهٔ سررسید گذشته و مانده: " + fa(result.totals.dueScheduleInvoices))
            Text("پرداخت هر قسط جداگانه ثبت نمی‌شود؛ این شمارش به معنی مبلغ دقیق معوق نیست.", style = MaterialTheme.typography.bodySmall)
            Text("تخفیف اقلام، گروه کالا، پرداخت‌های خروجی و تاریخ تک‌تک دریافت‌ها در مدل فعلی ثبت نمی‌شوند.", style = MaterialTheme.typography.bodySmall)
            }
            if(section == 1) {
            FormSection("عملکرد فروشگاه‌ها در بازه انتخاب‌شده") {
                if(result.branches.isEmpty()) Text("فروشی ثبت نشده است.")
                StoreTable(listOf("فروشگاه / نمایشگاه", "فروش · تومان", "دریافت · تومان", "مانده · تومان"),
                    result.branches.map { listOf(it.name, quantity(it.totals.payable), quantity(it.totals.received), quantity(it.totals.balance)) }, listOf(1.4f, 1f, 1f, 1f))
            }
            ChoiceEntry("مقایسه با", state.stores.firstOrNull { it.id == filter.comparisonStoreId }?.name ?: "دوره قبل · همین فروشگاه‌ها", listOf(null to "دوره قبل · همین فروشگاه‌ها") + state.stores.map { it.id to it.name }) { id -> vm.change { it.copy(comparisonStoreId = id, comparisonFrom = null, comparisonTo = null) } }
            Text(if(filter.comparisonStoreId == null) "مقایسهٔ دوره‌ها" else "مقایسهٔ فروشگاه‌ها", style = MaterialTheme.typography.titleLarge)
            DateEntry("شروع دورهٔ مقایسه", result.comparisonFrom.toString()) { date -> vm.change { it.copy(comparisonFrom = java.time.LocalDate.parse(date), comparisonTo = result.comparisonTo) } }
            DateEntry("پایان دورهٔ مقایسه", result.comparisonTo.toString()) { date -> vm.change { it.copy(comparisonFrom = result.comparisonFrom, comparisonTo = java.time.LocalDate.parse(date)) } }
            Text("دورهٔ مقایسه با همین فیلتر مشتری/کالا/تسویه: " + money(result.comparison.payable) + " — " + fa(result.comparison.count) + " فاکتور")
            StoreTable(listOf("معیار", "انتخاب فعلی", "مقایسه"), listOf(
                listOf("فروش · تومان", quantity(result.totals.payable), quantity(result.comparison.payable)),
                listOf("دریافت · تومان", quantity(result.totals.received), quantity(result.comparison.received)),
                listOf("تعداد فاکتور", quantity(result.totals.count), quantity(result.comparison.count))))
            DataRow("اختلاف فروش", money(Math.subtractExact(result.totals.payable, result.comparison.payable)), true)
            ReportChoice("گروه‌بندی روند", period(filter.period), ReportPeriod.entries.map { it to period(it) }) { value -> vm.change { it.copy(period = value) } }
            Text("روند مبلغ قابل پرداخت — تومان", style = MaterialTheme.typography.titleLarge)
            if(result.trend.isEmpty()) Text("در این بازه داده‌ای برای نمودار وجود ندارد.")
            val max = (result.trend + result.comparisonTrend).maxOfOrNull { it.amount }?.coerceAtLeast(1) ?: 1
            result.trend.forEach { bucket ->
                Text(PersianNumberFormatter.toPersianDigits(bucket.label) + " — " + money(bucket.amount) + " — " + fa(bucket.count) + " فاکتور")
                LinearProgressIndicator(progress = { (bucket.amount.toDouble() / max).toFloat().coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth())
            }
            if(result.trend.isNotEmpty()) Text("طول میله از صفر تا " + money(max) + " است؛ تاریخ‌ها شمسی و شروع هفته شنبه است.", style = MaterialTheme.typography.bodySmall)
            FormSection("روند مقایسه · " + (state.stores.firstOrNull { it.id == filter.comparisonStoreId }?.name ?: "دوره قبل")) {
                if(result.comparisonTrend.isEmpty()) Text("در دورهٔ مقایسه داده‌ای ثبت نشده است.")
                result.comparisonTrend.forEach { bucket ->
                    DataRow(bucket.label,money(bucket.amount))
                    LinearProgressIndicator(progress = { (bucket.amount.toDouble()/max).toFloat().coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.tertiary)
                }
            }
            }
            if(section == 2) {
            if(filter.document == ReportDocument.SALE) {
                FormSection("کالاها · پیش از تخفیف و مالیات") {
                    result.products.forEach { item -> DataRow(item.name + " · " + fa(item.quantity) + " عدد",money(item.gross)); HorizontalDivider() }
                }
            }
            FormSection("مشتریان") {
                result.customers.forEach { customer ->
                    Text(customer.name,style = MaterialTheme.typography.titleSmall)
                    DataRow(fa(customer.count) + " فاکتور",money(customer.payable))
                    DataRow("مانده",money(customer.balance)); HorizontalDivider()
                }
            }
            }
            if(section == 0) {
            Text("جزئیات فاکتورها", style = MaterialTheme.typography.titleLarge)
            if(result.rows.isEmpty()) Text("فاکتوری مطابق این فیلترها پیدا نشد.")
            result.rows.forEach { invoice ->
                OutlinedCard(onClick = { onDetail(invoice.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("نام مشتری: " + invoice.customer.name, style = MaterialTheme.typography.titleSmall)
                        Text(invoice.store.name + " · " + dateLabel(invoice.date))
                        Text("کد رهگیری: " + ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(invoice), style = MaterialTheme.typography.labelMedium)
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
            }
            Button(modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = !exporting, onClick = { exportSnapshot = result; save.launch("store-report.pdf") }) { Text("ذخیرهٔ PDF گزارش کامل") }
            if(exporting) LinearProgressIndicator(Modifier.fillMaxWidth())
            exportMessage?.let { Text(it) }
            }
        }
    }
}

@Composable private fun Summary(t: StoreReportTotals) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    OutlinedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("تعداد فاکتور: " + fa(t.count))
        Text("مبلغ قابل پرداخت: " + money(t.payable), style = MaterialTheme.typography.titleLarge)
        Text("دریافتی فعلی: " + money(t.received) + " — مانده: " + money(t.balance))
        TextButton(onClick = { expanded = !expanded }) { Text(if(expanded) "خلاصه‌تر" else "جزئیات مبالغ") }
        if(expanded) {
        Text("ناخالص کالا: " + money(t.grossItems))
        Text("تخفیف فاکتور: " + money(t.discount) + " — مالیات: " + money(t.tax))
        Text("افزودهٔ اقساط: " + money(t.financeCharge))
        }
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
private fun fa(value: Number) = quantity(value)
private fun settlement(value: ReportSettlement) = when(value) { ReportSettlement.ALL -> "همه"; ReportSettlement.SETTLED -> "تسویه‌شده"; ReportSettlement.OUTSTANDING -> "دارای مانده"; ReportSettlement.INSTALLMENT -> "اقساطی" }
private fun period(value: ReportPeriod) = when(value) { ReportPeriod.DAY -> "روزانه"; ReportPeriod.WEEK -> "هفتگی"; ReportPeriod.MONTH -> "ماهانه"; ReportPeriod.YEAR -> "سالانه" }
private fun order(value: ReportOrder) = when(value) { ReportOrder.NEWEST -> "جدیدترین"; ReportOrder.OLDEST -> "قدیمی‌ترین"; ReportOrder.AMOUNT_DESC -> "بیشترین مبلغ"; ReportOrder.BALANCE_DESC -> "بیشترین مانده" }

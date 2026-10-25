package ir.siamak.fintrack.store.presentation.components
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.formatter.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.*
import java.text.NumberFormat
import java.util.Locale

fun money(value: Long) = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.format(value) + " تومان"
fun quantity(value: Number) = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.toPersianDigits(NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 8 }.format(value))
fun dateLabel(value: String) = runCatching { DateFormatter.formatJalali(LocalDate.parse(value.take(10))) }.getOrDefault(value)
fun numeric(value: String): String = value.filterNot { it == ',' || it == '٬' }.map { c -> if(c.isDigit()) Character.getNumericValue(c).toString() else c.toString() }.joinToString("")
fun whole(value: String, optional: Boolean = false): Long = if(optional && value.isBlank()) 0 else numeric(value).toLongOrNull() ?: error("مبلغ یا تعداد صحیح وارد کنید")
fun decimal(value: String): Double = numeric(value).replace('٫','.').toDoubleOrNull()?.takeIf { it.isFinite() } ?: error("مقدار عددی معتبر وارد کنید")

@Composable fun StorePage(title: String, loading: Boolean = false, error: String? = null, retry: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 760.dp).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            if(loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            if(error != null) {
                Text(error, color = MaterialTheme.colorScheme.error)
                retry?.let { TextButton(onClick = it) { Text("تلاش مجدد") } }
            }
            content()
            Spacer(Modifier.height(24.dp))
        }
        }
        }
    }
}
@Composable fun Entry(label: String, value: String, change: (String) -> Unit, number: Boolean = false, lines: Int = 1) =
    OutlinedTextField(value = value, onValueChange = { change(if(number) numeric(it) else it) }, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
        visualTransformation = if(number) ir.siamak.fintrack.common.core.formatter.GroupedNumberTransformation else ir.siamak.fintrack.common.core.formatter.PersianVisualTransformation,
        singleLine = lines == 1, minLines = lines, keyboardOptions = KeyboardOptions(keyboardType = if(number) KeyboardType.Decimal else KeyboardType.Text))


@Composable fun DateEntry(label: String, value: String, change: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) { Text(label + ": " + dateLabel(value)) }
    if(open) {
        val initial = runCatching { ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter.fromGregorian(LocalDate.parse(value)) }.getOrNull()
        val state = ir.siamak.fintrack.common.core.datepicker.state.rememberPersianDatePickerState(initialSelectedDate = initial)
        ir.siamak.fintrack.common.core.datepicker.components.PersianDatePickerDialog(
            visible = true, displayedMonth = state.displayedMonth, selectedDate = state.selectedDate,
            onDateSelected = state::onDateSelected, onPreviousMonthClick = state::showPreviousMonth, onNextMonthClick = state::showNextMonth,
            onDismissRequest = { open = false }, onConfirmClick = {
                state.selectedDate?.let { change(ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter.toGregorian(it).toString()) }
                open = false
            }, title = label)
    }
}
@Composable fun Picture(value: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<android.graphics.Bitmap?>(null, value) {
        this.value = withContext(Dispatchers.IO) { runCatching {
            if(value.isNullOrBlank()) null else context.contentResolver.openInputStream(Uri.parse(value))?.use {
                val options = BitmapFactory.Options().apply { inSampleSize = 4 }
                BitmapFactory.decodeStream(it, null, options)
            }
        }.getOrNull() }
    }
    bitmap?.let { Image(it.asImageBitmap(), "تصویر", modifier.size(80.dp)) }
}
@Composable fun ImageEntry(label: String, value: String?, change: (String?) -> Unit) {
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            runCatching { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION); change(it.toString()) }
                .onFailure { error = "دسترسی پایدار به تصویر ممکن نشد" }
        }
    }
    OutlinedCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Picture(value, Modifier.size(112.dp))
        Button(onClick = { launcher.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth()) { Text(label) }
        if(value != null) FilledTonalButton(onClick = { change(null) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)) { Text("حذف تصویر") }
    } }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}
@Composable fun ConfirmDelete(name: String, confirm: () -> Unit, dismiss: () -> Unit, error: String? = null, busy: Boolean = false) =
    AlertDialog(onDismissRequest = { if(!busy) dismiss() }, title = { Text("حذف " + name) }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("اطلاعات فاکتورهای قبلی محفوظ می‌ماند. حذف شود؟")
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    } }, confirmButton = { TextButton(enabled = !busy, onClick = confirm) { Text("حذف") } }, dismissButton = { TextButton(enabled = !busy, onClick = dismiss) { Text("انصراف") } })

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun StorePagePreview() { MaterialTheme { StorePage("حسابدار فروشگاه") { Text("اطلاعات فروشگاه") } } }

@Composable fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    } }
}
@Composable fun <T> ChoiceEntry(title: String, selected: String, options: List<Pair<T,String>>, change: (T) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), onClick = { open = true }, shape = MaterialTheme.shapes.small) {
            Text(title + ": " + selected, Modifier.weight(1f)); Text("⌄")
        }
        DropdownMenu(open, { open = false }) { options.forEach { (value,label) -> DropdownMenuItem(text = { Text(label) }, onClick = { open = false; change(value) }) } }
    }
}
@Composable fun DataRow(label: String, value: String, strong: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, Modifier.weight(1.3f), style = if(strong) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}
@Composable fun ItemTable(items: List<ir.siamak.fintrack.store.domain.model.SaleItem>) {
    FormSection("اقلام فاکتور") {
        Row(Modifier.fillMaxWidth()) { Text("کالا", Modifier.weight(1.5f)); Text("تعداد", Modifier.weight(.6f)); Text("جمع · تومان", Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End) }
        HorizontalDivider()
        items.forEach { item ->
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1.5f)) { Text(item.productName); Text(money(item.unitPrice), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Text(quantity(item.quantity), Modifier.weight(.6f))
                Text(quantity(Math.multiplyExact(item.quantity.toLong(), item.unitPrice)), Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .5f))
        }
    }
}
@Composable fun ActionPair(createLabel: String, invoicesLabel: String, create: () -> Unit, invoices: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(onClick = create, modifier = Modifier.weight(1f).height(120.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("＋", style = MaterialTheme.typography.headlineMedium); Text(createLabel,style = MaterialTheme.typography.titleSmall)
            }
        }
        OutlinedCard(onClick = invoices, modifier = Modifier.weight(1f).height(120.dp)) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("≡",style = MaterialTheme.typography.headlineMedium); Text(invoicesLabel,style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

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

fun money(value: Long) = NumberFormat.getIntegerInstance(Locale.forLanguageTag("fa")).format(value) + " تومان"
fun dateLabel(value: String) = runCatching { DateFormatter.formatJalali(LocalDate.parse(value.take(10))) }.getOrDefault(value)
fun numeric(value: String): String = value.map { c -> if(c.isDigit()) Character.getNumericValue(c).toString() else c.toString() }.joinToString("")
fun whole(value: String, optional: Boolean = false): Long = if(optional && value.isBlank()) 0 else numeric(value).toLongOrNull() ?: error("مبلغ یا تعداد صحیح وارد کنید")
fun decimal(value: String): Double = numeric(value).replace('٫','.').toDoubleOrNull()?.takeIf { it.isFinite() } ?: error("مقدار عددی معتبر وارد کنید")

@Composable fun StorePage(title: String, loading: Boolean = false, error: String? = null, retry: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 960.dp).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
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
@Composable fun Entry(label: String, value: String, change: (String) -> Unit, number: Boolean = false, lines: Int = 1) =
    OutlinedTextField(value = value, onValueChange = change, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
        visualTransformation = ir.siamak.fintrack.common.core.formatter.PersianVisualTransformation,
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
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Picture(value)
        TextButton(onClick = { launcher.launch(arrayOf("image/*")) }) { Text(label) }
        if(value != null) TextButton(onClick = { change(null) }) { Text("حذف تصویر") }
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}
@Composable fun ConfirmDelete(name: String, confirm: () -> Unit, dismiss: () -> Unit) =
    AlertDialog(onDismissRequest = dismiss, title = { Text("حذف " + name) }, text = { Text("اطلاعات فاکتورهای قبلی محفوظ می‌ماند. حذف شود؟") },
        confirmButton = { TextButton(onClick = confirm) { Text("حذف") } }, dismissButton = { TextButton(onClick = dismiss) { Text("انصراف") } })

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun StorePagePreview() { MaterialTheme { StorePage("حسابدار فروشگاه") { Text("اطلاعات فروشگاه") } } }

package ir.siamak.fintrack.common.core.datepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth

@Composable
fun PersianCalendarHeader(
    month: PersianMonth,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMonthSelected: ((PersianMonth) -> Unit)? = null
) {
    var choosing by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onNextMonthClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "ماه بعد"
            )
        }

        TextButton(onClick = { choosing = true },enabled = onMonthSelected != null) {
            Text(text = PersianCalendarFormatter.formatMonthYear(month),groupNumbers = false,
                style = MaterialTheme.typography.titleMedium,color = MaterialTheme.colorScheme.onSurface)
        }

        IconButton(onClick = onPreviousMonthClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "ماه قبل"
            )
        }
    }
    if(choosing && onMonthSelected != null) MonthYearDialog(month,{ choosing = false }) {
        onMonthSelected(it); choosing = false
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun MonthYearDialog(current: PersianMonth, dismiss: () -> Unit, select: (PersianMonth) -> Unit) {
    var year by rememberSaveable { mutableStateOf(current.year.toString()) }
    var month by rememberSaveable { mutableIntStateOf(current.month) }
    val validYear = year.toIntOrNull()?.takeIf { it in 1..3000 }
    AlertDialog(onDismissRequest = dismiss,title = { Text("انتخاب ماه و سال") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = year,onValueChange = { value ->
                year = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(value).filter(Char::isDigit).take(4)
            },label = { Text("سال شمسی") },singleLine = true,
                visualTransformation = ir.siamak.fintrack.common.core.formatter.PersianVisualTransformation,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { year = ((validYear ?: current.year)-1).coerceAtLeast(1).toString() }) { Text("سال قبل") }
                TextButton(onClick = { year = ((validYear ?: current.year)+1).coerceAtMost(3000).toString() }) { Text("سال بعد") }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp),maxItemsInEachRow = 3) {
                (1..12).forEach { value -> FilterChip(selected = month == value,onClick = { month = value },
                    label = { Text(PersianCalendarFormatter.getMonthName(value)) }) }
            }
        } },confirmButton = { TextButton(onClick = { validYear?.let { select(PersianMonth(it,month)) } },enabled = validYear != null) { Text("نمایش ماه") } },
        dismissButton = { TextButton(onClick = dismiss) { Text("انصراف") } })
}


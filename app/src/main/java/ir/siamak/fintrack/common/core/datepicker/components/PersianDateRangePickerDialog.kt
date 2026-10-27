package ir.siamak.fintrack.common.core.datepicker.components

import androidx.compose.material3.AlertDialog
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.state.PersianDateRangePickerState

@Composable
fun PersianDateRangePickerDialog(
    visible: Boolean,
    state: PersianDateRangePickerState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (!visible) return

    var singleDay by rememberSaveable { mutableStateOf(state.selectedRange.start != null && state.selectedRange.start == state.selectedRange.end) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("انتخاب تاریخ گزارش") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(singleDay, { singleDay = true; state.selectedRange.start?.let { state.setSelection(PersianDateRange(it,it)) } }, { Text("یک روز") })
                FilterChip(!singleDay, { singleDay = false }, { Text("بازه زمانی") })
            }
            PersianDateRangePicker(
                displayedMonth = state.displayedMonth,
                selectedRange = state.selectedRange,
                onDayClick = { if(singleDay) state.setSelection(PersianDateRange(it,it)) else state.onDateSelected(it) },
                onPreviousMonthClick = { state.showPreviousMonth() },
                onNextMonthClick = { state.showNextMonth() }
            )
            }
        },
        confirmButton = {
            TextButton(enabled = state.selectedRange.start != null, onClick = { if(state.selectedRange.end == null) state.selectedRange.start?.let { state.setSelection(PersianDateRange(it,it)) }; onConfirmClick() }) { Text("تأیید") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("انصراف") }
        }
    )
}


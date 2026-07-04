package ir.siamak.fintrack.core.datepicker.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.state.PersianDateRangePickerState

@Composable
fun PersianDateRangePickerDialog(
    visible: Boolean,
    state: PersianDateRangePickerState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("انتخاب بازه زمانی") },
        text = {
            // نمایش کامپوننت تقویم
            PersianDateRangePicker(
                displayedMonth = state.displayedMonth,
                selectedRange = state.selectedRange,
                onDayClick = { state.onDateSelected(it) },
                onPreviousMonthClick = { state.showPreviousMonth() },
                onNextMonthClick = { state.showNextMonth() }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) { Text("تأیید") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("انصراف") }
        }
    )
}

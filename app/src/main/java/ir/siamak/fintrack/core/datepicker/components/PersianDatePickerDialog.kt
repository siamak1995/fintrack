package ir.siamak.fintrack.core.datepicker.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianMonth

@Composable
fun PersianDatePickerDialog(
    visible: Boolean,
    displayedMonth: PersianMonth,
    selectedDate: PersianDate?,
    onDateSelected: (PersianDate) -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    title: String = "انتخاب تاریخ"
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            PersianDatePicker(
                displayedMonth = displayedMonth,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text("تأیید")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("انصراف")
            }
        }
    )
}

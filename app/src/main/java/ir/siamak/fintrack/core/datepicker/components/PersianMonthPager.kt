package ir.siamak.fintrack.core.datepicker.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianMonth

/**
 * Temporary pager wrapper for month navigation.
 *
 * This currently delegates to [PersianDatePicker] and keeps a stable API
 * for future pager-based implementations.
 */
@Composable
fun PersianMonthPager(
    displayedMonth: PersianMonth,
    selectedDate: PersianDate?,
    onDateSelected: (PersianDate) -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PersianDatePicker(
        displayedMonth = displayedMonth,
        selectedDate = selectedDate,
        onDateSelected = onDateSelected,
        onPreviousMonthClick = onPreviousMonthClick,
        onNextMonthClick = onNextMonthClick,
        modifier = modifier
    )
}

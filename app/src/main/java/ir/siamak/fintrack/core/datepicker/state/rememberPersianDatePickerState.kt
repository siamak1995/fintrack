package ir.siamak.fintrack.core.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.model.PersianMonth

/**
 * Remembers a [PersianDatePickerState] across recompositions.
 */
@Composable
fun rememberPersianDatePickerState(
    initialSelectedDate: PersianDate? = null,
    initialDisplayedMonth: PersianMonth? = initialSelectedDate?.toPersianMonth(),
    minDate: PersianDate? = null,
    maxDate: PersianDate? = null
): PersianDatePickerState {
    return remember(
        initialSelectedDate,
        initialDisplayedMonth,
        minDate,
        maxDate
    ) {
        PersianDatePickerState(
            initialSelectedDate = initialSelectedDate,
            initialDisplayedMonth = initialDisplayedMonth,
            minDate = minDate,
            maxDate = maxDate
        )
    }
}

/**
 * Remembers a [PersianDateRangePickerState] across recompositions.
 */
@Composable
fun rememberPersianDateRangePickerState(
    initialRange: PersianDateRange = PersianDateRange(),
    initialDisplayedMonth: PersianMonth? = initialRange.start?.toPersianMonth(),
    minDate: PersianDate? = null,
    maxDate: PersianDate? = null
): PersianDateRangePickerState {
    return remember(
        initialRange,
        initialDisplayedMonth,
        minDate,
        maxDate
    ) {
        PersianDateRangePickerState(
            initialSelectedRange = initialRange,
            initialDisplayedMonth = initialDisplayedMonth,
            minDate = minDate,
            maxDate = maxDate
        )
    }
}
package ir.siamak.fintrack.core.datepicker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.siamak.fintrack.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.core.datepicker.calendar.JalaliMonthCalculator
import ir.siamak.fintrack.core.datepicker.mapper.PersianCalendarUiMapper
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.model.PersianMonth

/**
 * Persian calendar picker for range selection.
 */
@Composable
fun PersianDateRangePicker(
    displayedMonth: PersianMonth,
    selectedRange: PersianDateRange,
    onDayClick: (PersianDate) -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
    minDate: PersianDate? = null,
    maxDate: PersianDate? = null,
    today: PersianDate = JalaliCalendarEngine.today()
) {
    val days = JalaliMonthCalculator.getMonthDays(displayedMonth)
    val dayItems = PersianCalendarUiMapper.mapMonthDays(
        displayedMonth = displayedMonth,
        days = days,
        selectedRange = selectedRange,
        today = today,
        minDate = minDate,
        maxDate = maxDate
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        PersianCalendarHeader(
            month = displayedMonth,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )

        PersianWeekHeader()

        PersianMonthGrid(
            items = dayItems,
            onDayClick = onDayClick
        )
    }
}

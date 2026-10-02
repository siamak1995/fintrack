package ir.siamak.fintrack.common.core.datepicker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliMonthCalculator
import ir.siamak.fintrack.common.core.datepicker.mapper.PersianCalendarUiMapper
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth

@Composable
fun PersianDatePicker(
    displayedMonth: PersianMonth,
    selectedDate: PersianDate?,
    onDateSelected: (PersianDate) -> Unit,
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
        selectedDate = selectedDate,
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
            onDayClick = onDateSelected
        )
    }
}


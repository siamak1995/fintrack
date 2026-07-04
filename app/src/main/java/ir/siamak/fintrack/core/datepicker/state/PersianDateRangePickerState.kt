package ir.siamak.fintrack.core.datepicker.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ir.siamak.fintrack.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.model.PersianMonth

/**
 * State holder for Persian date range selection.
 */
class PersianDateRangePickerState(
    initialSelectedRange: PersianDateRange = PersianDateRange(),
    initialDisplayedMonth: PersianMonth? = initialSelectedRange.start?.toPersianMonth()
        ?: JalaliCalendarEngine.currentMonth(),
    val minDate: PersianDate? = null,
    val maxDate: PersianDate? = null
) {
    var selectedRange by mutableStateOf(initialSelectedRange)
        private set

    var displayedMonth by mutableStateOf(
        initialDisplayedMonth ?: JalaliCalendarEngine.currentMonth()
    )
        private set

    fun onDateSelected(date: PersianDate) {
        if (!isSelectable(date)) return

        val start = selectedRange.start
        val end = selectedRange.end

        selectedRange = when {
            start == null || (start != null && end != null) -> {
                PersianDateRange(start = date, end = null)
            }
            date < start -> {
                PersianDateRange(start = date, end = start)
            }
            else -> {
                PersianDateRange(start = start, end = date)
            }
        }

        displayedMonth = date.toPersianMonth()
    }

    fun updateDisplayedMonth(month: PersianMonth) {
        displayedMonth = month
    }

    fun showPreviousMonth() {
        displayedMonth = JalaliCalendarEngine.previousMonth(displayedMonth)
    }

    fun showNextMonth() {
        displayedMonth = JalaliCalendarEngine.nextMonth(displayedMonth)
    }

    fun clearSelection() {
        selectedRange = PersianDateRange()
    }

    fun setSelection(range: PersianDateRange) {
        val start = range.start
        val end = range.end

        if (start != null && !isSelectable(start)) return
        if (end != null && !isSelectable(end)) return

        selectedRange = range

        displayedMonth = start?.toPersianMonth()
            ?: initialMonthFallback()
    }

    fun isSelected(date: PersianDate): Boolean {
        return selectedRange.start == date || selectedRange.end == date
    }

    fun isInRange(date: PersianDate): Boolean {
        val start = selectedRange.start
        val end = selectedRange.end
        return start != null && end != null && date >= start && date <= end
    }

    fun isSelectable(date: PersianDate): Boolean {
        if (!JalaliCalendarEngine.isValidDate(date)) return false

        val beforeMin = minDate?.let { date < it } == true
        val afterMax = maxDate?.let { date > it } == true

        return !beforeMin && !afterMax
    }

    private fun initialMonthFallback(): PersianMonth {
        return JalaliCalendarEngine.currentMonth()
    }
}

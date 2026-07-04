package ir.siamak.fintrack.core.datepicker.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ir.siamak.fintrack.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.model.PersianMonth

/**
 * State holder for range-based Persian calendar selection.
 */
class PersianDateRangePickerState(
    initialRange: PersianDateRange = PersianDateRange(),
    initialDisplayedMonth: PersianMonth? = initialRange.start?.toPersianMonth()
        ?: JalaliCalendarEngine.currentMonth(),
    val minDate: PersianDate? = null,
    val maxDate: PersianDate? = null
) {
    var selectedRange by mutableStateOf(initialRange.normalized())
        private set

    var displayedMonth by mutableStateOf(
        initialDisplayedMonth ?: JalaliCalendarEngine.currentMonth()
    )
        private set

    fun onDateSelected(date: PersianDate) {
        if (!isSelectable(date)) return

        val current = selectedRange

        selectedRange = when {
            current.start == null -> {
                PersianDateRange(start = date, end = null)
            }

            current.start != null && current.end == null -> {
                if (date < current.start) {
                    PersianDateRange(start = date, end = current.start)
                } else {
                    PersianDateRange(start = current.start, end = date)
                }
            }

            else -> {
                PersianDateRange(start = date, end = null)
            }
        }

        displayedMonth = date.toPersianMonth()
    }

    fun setDisplayedMonth(month: PersianMonth) {
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
        val normalized = range.normalized()

        val startValid = normalized.start == null || isSelectable(normalized.start)
        val endValid = normalized.end == null || isSelectable(normalized.end)

        if (!startValid || !endValid) return

        selectedRange = normalized
        normalized.start?.let { start ->
            displayedMonth = start.toPersianMonth()
        }
    }

    fun isSelected(date: PersianDate): Boolean {
        return date == selectedRange.start || date == selectedRange.end
    }

    fun isInRange(date: PersianDate): Boolean {
        val start = selectedRange.start ?: return false
        val end = selectedRange.end ?: return false
        return date >= start && date <= end
    }

    fun isSelectable(date: PersianDate): Boolean {
        if (!JalaliCalendarEngine.isValidDate(date)) return false

        val beforeMin = minDate?.let { date < it } == true
        val afterMax = maxDate?.let { date > it } == true

        return !beforeMin && !afterMax
    }
}

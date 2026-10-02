package ir.siamak.fintrack.common.core.datepicker.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth

/**
 * State holder for single-date Persian calendar selection.
 */
class PersianDatePickerState(
    initialSelectedDate: PersianDate? = null,
    initialDisplayedMonth: PersianMonth? = initialSelectedDate?.toPersianMonth()
        ?: JalaliCalendarEngine.currentMonth(),
    val minDate: PersianDate? = null,
    val maxDate: PersianDate? = null
) {
    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    var displayedMonth by mutableStateOf(
        initialDisplayedMonth ?: JalaliCalendarEngine.currentMonth()
    )
        private set

    fun onDateSelected(date: PersianDate) {
        if (!isSelectable(date)) return
        selectedDate = date
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
        selectedDate = null
    }

    fun setSelection(date: PersianDate?) {
        if (date == null) {
            selectedDate = null
            return
        }

        if (!isSelectable(date)) return

        selectedDate = date
        displayedMonth = date.toPersianMonth()
    }

    fun isSelected(date: PersianDate): Boolean {
        return selectedDate == date
    }

    fun isSelectable(date: PersianDate): Boolean {
        if (!JalaliCalendarEngine.isValidDate(date)) return false

        val beforeMin = minDate?.let { date < it } == true
        val afterMax = maxDate?.let { date > it } == true

        return !beforeMin && !afterMax
    }
}


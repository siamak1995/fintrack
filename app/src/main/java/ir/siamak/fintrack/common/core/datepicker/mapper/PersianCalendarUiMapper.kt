package ir.siamak.fintrack.common.core.datepicker.mapper

import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth

/**
 * UI-ready state for a single day cell in the Persian calendar grid.
 */
data class PersianDayCellUiModel(
    val date: PersianDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isInRange: Boolean,
    val isRangeStart: Boolean,
    val isRangeEnd: Boolean,
    val isDisabled: Boolean = false
)

/**
 * Maps domain calendar models into UI models used by Compose components.
 */
object PersianCalendarUiMapper {

    fun mapMonthDays(
        displayedMonth: PersianMonth,
        days: List<PersianDate>,
        selectedDate: PersianDate? = null,
        selectedRange: PersianDateRange? = null,
        today: PersianDate? = null,
        minDate: PersianDate? = null,
        maxDate: PersianDate? = null
    ): List<PersianDayCellUiModel> {
        val normalizedRange = selectedRange?.normalized()

        return days.map { date ->
            val isCurrentMonth = date.year == displayedMonth.year && date.month == displayedMonth.month
            val isRangeStart = normalizedRange?.start == date
            val isRangeEnd = normalizedRange?.end == date
            val isSelected = selectedDate == date || isRangeStart || isRangeEnd
            val isInRange = normalizedRange?.let { range ->
                range.start != null && range.end != null && date > range.start && date < range.end
            } ?: false
            val isToday = today == date
            val isDisabled = (minDate != null && date < minDate) || (maxDate != null && date > maxDate)

            PersianDayCellUiModel(
                date = date,
                isCurrentMonth = isCurrentMonth,
                isToday = isToday,
                isSelected = isSelected,
                isInRange = isInRange,
                isRangeStart = isRangeStart,
                isRangeEnd = isRangeEnd,
                isDisabled = isDisabled
            )
        }
    }
}


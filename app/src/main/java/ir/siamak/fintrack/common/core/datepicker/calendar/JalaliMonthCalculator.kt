package ir.siamak.fintrack.common.core.datepicker.calendar

import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth
import ir.siamak.fintrack.common.core.datepicker.model.PersianWeekDay

object JalaliMonthCalculator {

    private const val CALENDAR_GRID_SIZE = 42

    fun getMonthDays(month: PersianMonth): List<PersianDate> {
        val firstDayOfMonth = PersianDate(month.year, month.month, 1)
        val firstWeekDayIndex = getWeekDay(firstDayOfMonth).index
        val daysInCurrentMonth = JalaliCalendarEngine.getDaysInMonth(month.year, month.month)

        val previousMonth = JalaliCalendarEngine.previousMonth(month)
        val daysInPreviousMonth = JalaliCalendarEngine.getDaysInMonth(previousMonth.year, previousMonth.month)

        val result = mutableListOf<PersianDate>()

        val leadingDaysCount = firstWeekDayIndex
        for (i in leadingDaysCount downTo 1) {
            result.add(
                PersianDate(
                    year = previousMonth.year,
                    month = previousMonth.month,
                    day = daysInPreviousMonth - i + 1
                )
            )
        }

        for (day in 1..daysInCurrentMonth) {
            result.add(PersianDate(month.year, month.month, day))
        }

        val trailingDaysCount = CALENDAR_GRID_SIZE - result.size
        val nextMonth = JalaliCalendarEngine.nextMonth(month)

        for (day in 1..trailingDaysCount) {
            result.add(PersianDate(nextMonth.year, nextMonth.month, day))
        }

        return result
    }

    fun getWeekDay(date: PersianDate): PersianWeekDay {
        val localDate = JalaliDateConverter.toGregorian(date)
        return when (localDate.dayOfWeek.value) {
            6 -> PersianWeekDay.SATURDAY
            7 -> PersianWeekDay.SUNDAY
            1 -> PersianWeekDay.MONDAY
            2 -> PersianWeekDay.TUESDAY
            3 -> PersianWeekDay.WEDNESDAY
            4 -> PersianWeekDay.THURSDAY
            5 -> PersianWeekDay.FRIDAY
            else -> PersianWeekDay.SATURDAY
        }
    }

    fun getWeekDaysOrdered(): List<PersianWeekDay> {
        return PersianWeekDay.ordered
    }
}


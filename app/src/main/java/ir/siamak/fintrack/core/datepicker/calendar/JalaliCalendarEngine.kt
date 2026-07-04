package ir.siamak.fintrack.core.datepicker.calendar

import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianMonth
import java.time.LocalDate

object JalaliCalendarEngine {

    fun isLeapYear(year: Int): Boolean {
        require(year > 0) { "Year must be greater than 0." }

        val epBase = year - if (year >= 0) 474 else 473
        val epYear = 474 + mod(epBase, 2820)

        return ((epYear * 682) % 2816) < 682
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        require(year > 0) { "Year must be greater than 0." }
        require(month in 1..12) { "Month must be between 1 and 12." }

        return when (month) {
            in 1..6 -> 31
            in 7..11 -> 30
            12 -> if (isLeapYear(year)) 30 else 29
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    fun isValidDate(date: PersianDate): Boolean {
        if (date.year <= 0) return false
        if (date.month !in 1..12) return false
        if (date.day <= 0) return false

        return date.day <= getDaysInMonth(date.year, date.month)
    }

    fun requireValidDate(date: PersianDate): PersianDate {
        require(isValidDate(date)) {
            "Invalid Persian date: ${date.toCompactString()}"
        }
        return date
    }

    fun today(): PersianDate {
        return JalaliDateConverter.fromGregorian(LocalDate.now())
    }

    fun currentMonth(): PersianMonth {
        val today = today()
        return PersianMonth(today.year, today.month)
    }

    fun previousMonth(month: PersianMonth): PersianMonth {
        return if (month.month == 1) {
            PersianMonth(month.year - 1, 12)
        } else {
            PersianMonth(month.year, month.month - 1)
        }
    }

    fun nextMonth(month: PersianMonth): PersianMonth {
        return if (month.month == 12) {
            PersianMonth(month.year + 1, 1)
        } else {
            PersianMonth(month.year, month.month + 1)
        }
    }

    fun clampDay(year: Int, month: Int, day: Int): Int {
        return day.coerceIn(1, getDaysInMonth(year, month))
    }

    private fun mod(a: Int, b: Int): Int = ((a % b) + b) % b
}

package ir.siamak.fintrack.common.core.datepicker.calendar

import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth
import ir.siamak.fintrack.common.core.datepicker.model.PersianWeekDay

object PersianCalendarFormatter {

    private val monthNames = listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )

    private val shortWeekDayNames = mapOf(
        PersianWeekDay.SATURDAY to "ش",
        PersianWeekDay.SUNDAY to "ی",
        PersianWeekDay.MONDAY to "د",
        PersianWeekDay.TUESDAY to "س",
        PersianWeekDay.WEDNESDAY to "چ",
        PersianWeekDay.THURSDAY to "پ",
        PersianWeekDay.FRIDAY to "ج"
    )

    private val fullWeekDayNames = mapOf(
        PersianWeekDay.SATURDAY to "شنبه",
        PersianWeekDay.SUNDAY to "یکشنبه",
        PersianWeekDay.MONDAY to "دوشنبه",
        PersianWeekDay.TUESDAY to "سه‌شنبه",
        PersianWeekDay.WEDNESDAY to "چهارشنبه",
        PersianWeekDay.THURSDAY to "پنجشنبه",
        PersianWeekDay.FRIDAY to "جمعه"
    )

    fun formatMonthYear(month: PersianMonth): String {
        return "${getMonthName(month.month)} ${month.year}"
    }

    fun formatDate(date: PersianDate): String {
        return "%04d/%02d/%02d".format(date.year, date.month, date.day)
    }

    fun formatShortDate(date: PersianDate): String {
        return "%02d/%02d".format(date.month, date.day)
    }

    fun formatFullDate(date: PersianDate): String {
        val weekDay = JalaliMonthCalculator.getWeekDay(date)
        return "${getFullWeekDayName(weekDay)} ${date.day} ${getMonthName(date.month)} ${date.year}"
    }

    fun formatRange(range: PersianDateRange): String {
        val normalized = range.normalized()

        return when {
            normalized.start == null && normalized.end == null -> ""
            normalized.start != null && normalized.end == null -> formatDate(normalized.start)
            normalized.start == null && normalized.end != null -> formatDate(normalized.end)
            normalized.start == normalized.end -> formatDate(normalized.start!!)
            else -> "${formatDate(normalized.start!!)} - ${formatDate(normalized.end!!)}"
        }
    }

    fun getMonthName(month: Int): String {
        require(month in 1..12) { "Month must be between 1 and 12." }
        return monthNames[month - 1]
    }

    fun getShortWeekDayName(day: PersianWeekDay): String {
        return shortWeekDayNames.getValue(day)
    }

    fun getFullWeekDayName(day: PersianWeekDay): String {
        return fullWeekDayNames.getValue(day)
    }
}


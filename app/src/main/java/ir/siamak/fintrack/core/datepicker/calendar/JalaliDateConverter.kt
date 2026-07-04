package ir.siamak.fintrack.core.datepicker.calendar

import ir.siamak.fintrack.core.datepicker.model.PersianDate
import java.time.LocalDate

object JalaliDateConverter {

    fun fromGregorian(date: LocalDate): PersianDate {
        val gy = date.year
        val gm = date.monthValue
        val gd = date.dayOfMonth

        val gDayNo = gregorianToDayNumber(gy, gm, gd)
        return dayNumberToJalali(gDayNo)
    }

    fun toGregorian(date: PersianDate): LocalDate {
        JalaliCalendarEngine.requireValidDate(date)

        val dayNo = jalaliToDayNumber(date.year, date.month, date.day)
        val gregorian = dayNumberToGregorian(dayNo)

        return LocalDate.of(gregorian.year, gregorian.month, gregorian.day)
    }

    private fun gregorianToDayNumber(year: Int, month: Int, day: Int): Int {
        val gDaysInMonth = intArrayOf(
            31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31
        )

        var gy = year - 1600
        var gm = month - 1
        var gd = day - 1

        var dayNo = 365 * gy +
                (gy + 3) / 4 -
                (gy + 99) / 100 +
                (gy + 399) / 400

        for (i in 0 until gm) {
            dayNo += gDaysInMonth[i]
        }

        if (gm > 1 && isGregorianLeapYear(year)) {
            dayNo += 1
        }

        dayNo += gd
        return dayNo
    }

    private fun dayNumberToJalali(dayNumber: Int): PersianDate {
        var jDayNo = dayNumber - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        val jm: Int
        val jd: Int

        if (jDayNo < 186) {
            jm = 1 + jDayNo / 31
            jd = 1 + (jDayNo % 31)
        } else {
            jDayNo -= 186
            jm = 7 + jDayNo / 30
            jd = 1 + (jDayNo % 30)
        }

        return PersianDate(
            year = jy,
            month = jm,
            day = jd
        )
    }

    private fun jalaliToDayNumber(year: Int, month: Int, day: Int): Int {
        var jy = year - 979
        val jm = month - 1
        val jd = day - 1

        var dayNo = 365 * jy +
                (jy / 33) * 8 +
                ((jy % 33) + 3) / 4

        for (i in 0 until jm) {
            dayNo += if (i < 6) 31 else 30
        }

        dayNo += jd + 79
        return dayNo
    }

    private fun dayNumberToGregorian(dayNumber: Int): GregorianDate {
        val gDaysInMonth = intArrayOf(
            31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31
        )

        var dayNo = dayNumber

        var gy = 1600 + 400 * (dayNo / 146097)
        dayNo %= 146097

        var leap = true
        if (dayNo >= 36525) {
            dayNo--
            gy += 100 * (dayNo / 36524)
            dayNo %= 36524

            if (dayNo >= 365) {
                dayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (dayNo / 1461)
        dayNo %= 1461

        if (dayNo >= 366) {
            leap = false
            dayNo--
            gy += dayNo / 365
            dayNo %= 365
        }

        var i = 0
        while (dayNo >= gDaysInMonth[i] + if (i == 1 && leap) 1 else 0) {
            dayNo -= gDaysInMonth[i] + if (i == 1 && leap) 1 else 0
            i++
        }

        val gm = i + 1
        val gd = dayNo + 1

        return GregorianDate(
            year = gy,
            month = gm,
            day = gd
        )
    }

    private fun isGregorianLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private data class GregorianDate(
        val year: Int,
        val month: Int,
        val day: Int
    )
}

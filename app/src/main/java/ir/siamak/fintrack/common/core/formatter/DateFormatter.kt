package ir.siamak.fintrack.common.core.formatter

import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Converts standard dates to a Persian-digit Jalali date for presentation. */
object DateFormatter {
    /** Formats a Gregorian [date] as a Jalali date in the `yyyy/MM/dd` form. */
    fun formatJalali(date: LocalDate): String {
        val jalali = JalaliDateConverter.fromGregorian(date)
        val raw = "%04d/%02d/%02d".format(java.util.Locale.US, jalali.year, jalali.month, jalali.day)
        return PersianNumberFormatter.toPersianDigits(raw)
    }

    /** Formats an epoch timestamp in [zoneId] as a Jalali date. */
    fun formatJalali(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): String =
        formatJalali(Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate())
}

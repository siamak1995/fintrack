package ir.siamak.fintrack.common.core.extensions

import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Converts this [LocalDate] to epoch milliseconds at the start of the day
 * in the provided [zoneId].
 */
fun LocalDate.toEpochMilliAtStartOfDay(
    zoneId: ZoneId = ZoneId.systemDefault()
): Long {
    return atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()
}

/**
 * Converts epoch milliseconds to a [LocalDate] in the provided [zoneId].
 */
fun Long.toLocalDate(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()
}

/**
 * Formats a Persian date as `yyyy/MM/dd`.
 */
fun PersianDate.toDisplayString(): String {
    return "%04d/%02d/%02d".format(year, month, day)
}

/**
 * Converts a Persian date to a compact sortable integer representation.
 *
 * Example:
 * `1404/07/09 -> 14040709`
 */
fun PersianDate.toCompactInt(): Int {
    return year * 10000 + month * 100 + day
}

/**
 * Converts this [LocalDate] to UTC epoch milliseconds at the start of the day.
 */
fun LocalDate.toUtcEpochMilli(): Long {
    return atStartOfDay()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli()
}


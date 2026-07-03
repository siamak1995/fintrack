package ir.siamak.fintrack.core.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

fun Long.toLocalDateTime() =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

fun Long.toLocalTime() =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()

fun Long.toEpochDay(): Long =
    toLocalDate().toEpochDay()

fun Long.isToday(): Boolean =
    toLocalDate() == LocalDate.now()

fun Long.isYesterday(): Boolean =
    toLocalDate() == LocalDate.now().minusDays(1)

fun LocalDate.toMillis(): Long =
    atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
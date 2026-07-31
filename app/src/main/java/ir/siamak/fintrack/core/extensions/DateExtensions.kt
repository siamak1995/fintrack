package ir.siamak.fintrack.core.extensions

import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.data.model.Transaction
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

data class PersianMonthKey(
    val year: Int,
    val month: Int
) {
    val key: String
        get() = "$year-$month"

    val sortValue: Int
        get() = year * 12 + month
}

private val persianMonthNames = listOf(
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


fun timestampToPersianDate(timestamp: Long) = runCatching {
    val localDate = Instant
        .ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    JalaliDateConverter.fromGregorian(localDate)
}.getOrNull()

fun PersianMonthKey.displayName(): String {
    val monthName = persianMonthNames.getOrNull(month - 1) ?: "ماه نامشخص"
    return "$monthName ${year.toString().toPersianDigits()}"
}
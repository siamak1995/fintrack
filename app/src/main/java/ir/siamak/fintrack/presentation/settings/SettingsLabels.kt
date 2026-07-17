package ir.siamak.fintrack.presentation.settings

import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.presentation.theme.ThemeMode

/**
 * توابع کمکی برای نمایش label فارسی enumها در UI.
 */
fun ThemeMode.toDisplayName(): String = when (this) {
    ThemeMode.SYSTEM -> "سیستمی"
    ThemeMode.LIGHT -> "روشن"
    ThemeMode.DARK -> "تیره"
}

fun Currency.toDisplayName(): String = when (this) {
    Currency.TOMAN -> "تومان"
    Currency.RIAL -> "ریال"
    else -> name
}

fun AppLanguage.toDisplayName(): String = when (this) {
    AppLanguage.PERSIAN -> "فارسی"
    AppLanguage.ENGLISH -> "English"
}

fun FirstDayOfWeek.toDisplayName(): String = when (this) {
    FirstDayOfWeek.SATURDAY -> "شنبه"
    FirstDayOfWeek.SUNDAY -> "یکشنبه"
    FirstDayOfWeek.MONDAY -> "دوشنبه"
}

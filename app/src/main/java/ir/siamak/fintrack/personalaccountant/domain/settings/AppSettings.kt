package ir.siamak.fintrack.personalaccountant.domain.settings

import ir.siamak.fintrack.personalaccountant.data.model.Currency
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.AppLanguage
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.FirstDayOfWeek
import ir.siamak.fintrack.personalaccountant.presentation.theme.ThemeMode

data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val currency: Currency = Currency.TOMAN,
    val language: AppLanguage = AppLanguage.PERSIAN,
    val biometricEnabled: Boolean = false,
    val pinEnabled: Boolean = false,
    val notificationEnabled: Boolean = true,
    val installmentReminder: Boolean = true,
    val dailyReminder: Boolean = false,
    val budgetReminder: Boolean = true,
    val dynamicColor: Boolean = true,
    val firstDayOfWeek: FirstDayOfWeek = FirstDayOfWeek.SATURDAY
)


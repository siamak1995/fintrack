package ir.siamak.fintrack.presentation.baseinfo.settings

import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.presentation.theme.ThemeMode

/**
 * رویدادهای صفحه تنظیمات.
 */
sealed interface SettingsEvent {
    data object Load : SettingsEvent
    data object Save : SettingsEvent
    data object Reset : SettingsEvent
    data object ClearMessage : SettingsEvent

    data class ChangeTheme(val theme: ThemeMode) : SettingsEvent
    data class ChangeCurrency(val currency: Currency) : SettingsEvent
    data class ChangeLanguage(val language: AppLanguage) : SettingsEvent
    data class ToggleBiometric(val enabled: Boolean) : SettingsEvent
    data class TogglePin(val enabled: Boolean) : SettingsEvent
    data class ToggleDynamicColor(val enabled: Boolean) : SettingsEvent
    data class ChangeFirstDay(val day: FirstDayOfWeek) : SettingsEvent

    data class ToggleNotification(val enabled: Boolean) : SettingsEvent
    data class ToggleInstallmentReminder(val enabled: Boolean) : SettingsEvent
    data class ToggleDailyReminder(val enabled: Boolean) : SettingsEvent
    data class ToggleBudgetReminder(val enabled: Boolean) : SettingsEvent

    // رویدادهای مدیریت جریان پین و امنیت
    data object PinSetupDismissed : SettingsEvent
    data object PinSetupSuccess : SettingsEvent
    data class SetBiometricHardwareAvailable(val available: Boolean) : SettingsEvent
}

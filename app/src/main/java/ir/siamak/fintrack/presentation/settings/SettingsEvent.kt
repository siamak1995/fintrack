package ir.siamak.fintrack.presentation.settings

import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.presentation.theme.ThemeMode

/**
 * رویدادهای قابل انجام در صفحه تنظیمات.
 *
 * ViewModel با دریافت هر رویداد، وضعیت برنامه را
 * تغییر داده یا عملیات مربوطه را اجرا می‌کند.
 */
sealed interface SettingsEvent {

    /**
     * بارگذاری تنظیمات ذخیره شده.
     */
    data object Load : SettingsEvent

    /**
     * تغییر تم برنامه.
     */
    data class ChangeTheme(
        val theme: ThemeMode
    ) : SettingsEvent

    /**
     * تغییر واحد پول.
     */
    data class ChangeCurrency(
        val currency: Currency
    ) : SettingsEvent

    /**
     * تغییر زبان برنامه.
     */
    data class ChangeLanguage(
        val language: AppLanguage
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن ورود با اثر انگشت.
     */
    data class ToggleBiometric(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن PIN.
     */
    data class TogglePin(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن اعلان‌ها.
     */
    data class ToggleNotification(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن یادآوری اقساط.
     */
    data class ToggleInstallmentReminder(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن یادآوری روزانه.
     */
    data class ToggleDailyReminder(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن هشدار بودجه.
     */
    data class ToggleBudgetReminder(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * فعال یا غیرفعال کردن Dynamic Color.
     */
    data class ToggleDynamicColor(
        val enabled: Boolean
    ) : SettingsEvent

    /**
     * تغییر اولین روز هفته.
     */
    data class ChangeFirstDay(
        val day: FirstDayOfWeek
    ) : SettingsEvent

    /**
     * بازگردانی تنظیمات به حالت پیش‌فرض.
     */
    data object Reset : SettingsEvent

}
package ir.siamak.fintrack.presentation.baseinfo.settings

import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.presentation.theme.ThemeMode

/**
 * وضعیت UI صفحه تنظیمات.
 *
 * این state شامل:
 * - مقادیر فعلی قابل نمایش در فرم
 * - وضعیت ذخیره‌سازی
 * - پرچم وجود تغییرات
 * - پیام‌های موقت UI
 */
data class SettingsState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,
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
    val firstDayOfWeek: FirstDayOfWeek = FirstDayOfWeek.SATURDAY,
    val appVersion: String = "1.1.1",
    val message: String? = null,
    val error: String? = null
)

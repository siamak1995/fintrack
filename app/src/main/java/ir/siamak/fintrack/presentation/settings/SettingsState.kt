package ir.siamak.fintrack.presentation.settings

import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.presentation.theme.ThemeMode

/**
 * وضعیت صفحه تنظیمات.
 *
 * این کلاس تمام داده‌های موردنیاز UI را نگهداری می‌کند و
 * تنها منبع حقیقت (Single Source Of Truth) برای صفحه Settings است.
 *
 * @property isLoading وضعیت بارگذاری اطلاعات
 * @property theme تم انتخاب شده برنامه
 * @property currency واحد پول پیش‌فرض
 * @property language زبان برنامه
 * @property biometricEnabled فعال بودن ورود با اثر انگشت
 * @property pinEnabled فعال بودن قفل PIN
 * @property notificationEnabled فعال بودن اعلان‌ها
 * @property installmentReminder یادآوری اقساط
 * @property dailyReminder یادآوری روزانه
 * @property budgetReminder هشدار بودجه
 * @property dynamicColor فعال بودن رنگ پویا
 * @property firstDayOfWeek اولین روز هفته
 * @property appVersion نسخه برنامه
 * @property error متن خطا در صورت وجود
 */
data class SettingsState(

    val isLoading: Boolean = false,

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

    val appVersion: String = "1.0.0",

    val error: String? = null

)
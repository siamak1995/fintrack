package ir.siamak.fintrack.personalaccountant.presentation.dashboard

/**
 * وضعیت سلامت مالی کاربر
 *
 * برای رنگ‌بندی داشبورد،
 * کارت‌های خلاصه،
 * پیام‌های هوشمند
 * و انیمیشن‌ها استفاده می‌شود.
 */
enum class FinancialHealth {

    /**
     * عالی
     */
    EXCELLENT,

    /**
     * خوب
     */
    GOOD,

    /**
     * متوسط
     */
    WARNING,

    /**
     * بحرانی
     */
    DANGER
}

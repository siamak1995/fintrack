package ir.siamak.fintrack.presentation.dashboard

/**
 * رویدادهای قابل دریافت در صفحه داشبورد.
 *
 * Eventها نمایانگر تعاملاتی هستند که از UI به ViewModel ارسال می‌شوند؛
 * مثل تازه‌سازی اطلاعات یا اجرای یک عملیات از طرف کاربر.
 */
sealed class DashboardEvent {

    /**
     * درخواست بارگذاری یا تازه‌سازی داده‌های داشبورد.
     */
    data object RefreshData : DashboardEvent()
}

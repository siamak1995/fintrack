package ir.siamak.fintrack.presentation.dashboard



/**
 * Event:
 *لیست کارهایی است که کاربر می‌تواند انجام دهد. (مثلاً: دکمه افزودن هزینه کلیک شد، یا صفحه رفرش شد.)
 *
 * رویدادهای مربوط به صفحه داشبورد.
 *
 * این کلاس تمامی تعاملات کاربر در صفحه داشبورد را به صورت Sealed Class تعریف می‌کند
 * تا در ViewModel به راحتی قابل مدیریت و پردازش باشند.
 */
sealed class DashboardEvent {
    /**
     * رویداد درخواست برای تازه‌سازی داده‌های داشبورد.
     */
    object RefreshData : DashboardEvent()

    // در آینده می‌توانید رویدادهای بیشتری مانند زیر اضافه کنید:
    // data class DeleteWallet(val walletId: Long) : DashboardEvent()
}

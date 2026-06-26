package ir.siamak.fintrack.presentation.dashboard

import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت نمایشی صفحه داشبورد.
 *
 * این مدل، تمام داده‌هایی را که UI برای نمایش نیاز دارد در یک نقطه نگه می‌دارد.
 *
 * @property wallets لیست حساب‌های موجود
 * @property totalBalance مجموع موجودی همه حساب‌ها
 * @property isLoading وضعیت بارگذاری داده‌ها
 * @property error پیام خطا در صورت بروز مشکل
 */
data class DashboardState(
    val wallets: List<Wallet> = emptyList(),
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

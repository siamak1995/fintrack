package ir.siamak.fintrack.presentation.dashboard

import ir.siamak.fintrack.data.model.Wallet

/**
 * State:
 * نگهدارنده “داده‌های خام” صفحه است. (مثلاً: آیا لودینگ است؟ موجودی حساب چقدر است؟)
 *
 * مدل نمایشی (UI State) برای صفحه داشبورد.
 *
 * این کلاس وضعیت فعلی UI را توصیف می‌کند و توسط [DashboardViewModel]
 * برای به‌روزرسانی رابط کاربری (Compose) استفاده می‌شود.
 *
 * @property wallets لیست کیف ‌پول‌های موجود در دیتابیس.
 * @property totalBalance مجموع موجودی تمام کیف ‌پول‌ها.
 * @property isLoading نشان‌دهنده وضعیت بارگذاری داده‌ها.
 * @property error متن خطا در صورت بروز مشکل، یا null اگر خطایی وجود ندارد.
 */
data class DashboardState(
    val wallets: List<Wallet> = emptyList(),
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

package ir.siamak.fintrack.presentation.wallet.list

import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت نمایشی صفحه لیست حساب‌ها.
 *
 * این state تمام اطلاعات موردنیاز UI را برای نمایش صفحه حساب‌ها نگه می‌دارد.
 * هدف از این کلاس، متمرکز کردن وضعیت صفحه در یک مدل immutable است تا
 * مدیریت UI در Compose ساده‌تر، قابل پیش‌بینی‌تر و تست‌پذیرتر شود.
 *
 * @property wallets لیست حساب‌های ثبت‌شده
 * @property isLoading نشان می‌دهد داده‌ها در حال بارگذاری هستند
 * @property error پیام خطا در صورت بروز مشکل هنگام دریافت داده‌ها
 */
data class WalletUiState(
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

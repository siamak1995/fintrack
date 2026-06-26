package ir.siamak.fintrack.presentation.wallet

import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت رابط کاربری مربوط به Wallet.
 *
 * این کلاس تمام داده‌هایی را که صفحه کیف پول برای نمایش نیاز دارد
 * در یک ساختار واحد نگه می‌دارد.
 *
 * @property wallets لیست کیف پول‌ها
 * @property isLoading وضعیت در حال بارگذاری بودن داده‌ها
 * @property error پیام خطا در صورت بروز مشکل
 */
data class WalletUiState(
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

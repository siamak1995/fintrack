package ir.siamak.fintrack.presentation.dashboard

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت کامل صفحه داشبورد.
 *
 * Dashboard تنها یک لیست از کیف پول‌ها نیست.
 * این صفحه خلاصه‌ای از وضعیت مالی کاربر را نمایش می‌دهد.
 *
 * این State تمام داده‌هایی را که UI نیاز دارد نگهداری می‌کند.
 *
 * @property wallets لیست حساب‌ها
 * @property transactions تراکنش‌های ماه جاری
 * @property totalIncome مجموع درآمد ماه
 * @property totalExpense مجموع هزینه ماه
 * @property currentBalance موجودی فعلی
 * @property walletCount تعداد حساب‌ها
 * @property transactionCount تعداد تراکنش‌ها
 * @property isLoading وضعیت بارگذاری
 * @property error متن خطا
 */
data class DashboardState(

    val wallets: List<Wallet> = emptyList(),

    val transactions: List<Transaction> = emptyList(),

    val totalIncome: Double = 0.0,

    val totalExpense: Double = 0.0,

    val currentBalance: Double = 0.0,

    val walletCount: Int = 0,

    val transactionCount: Int = 0,

    val isLoading: Boolean = false,

    val error: String? = null

)
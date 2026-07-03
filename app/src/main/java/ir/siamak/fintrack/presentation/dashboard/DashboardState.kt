package ir.siamak.fintrack.presentation.dashboard

import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت کامل داشبورد.
 *
 * تمام اطلاعات موردنیاز UI در این کلاس نگهداری می‌شود.
 *
 * هیچ محاسبه‌ای نباید داخل Compose انجام شود.
 * همه مقادیر باید از ViewModel وارد شوند.
 */
data class DashboardState(

    /**
     * حساب‌ها
     */
    val wallets: List<Wallet> = emptyList(),

    /**
     * تراکنش‌های ماه جاری
     */
    val transactions: List<Transaction> = emptyList(),

    /**
     * فقط آخرین تراکنش‌ها
     */
    val recentTransactions: List<Transaction> = emptyList(),

    /**
     * اعضای خانواده
     */
    val members: List<Member> = emptyList(),

    /**
     * اقساط
     */
    val installments: List<Installment> = emptyList(),

    /**
     * درآمد ماه جاری
     */
    val monthlyIncome: Double = 0.0,

    /**
     * هزینه ماه جاری
     */
    val monthlyExpense: Double = 0.0,

    /**
     * موجودی واقعی
     *
     * مجموع درآمد
     * منهای هزینه
     */
    val totalBalance: Double = 0.0,

    /**
     * مجموع موجودی کیف پول‌ها
     */
    val walletBalance: Double = 0.0,

    val userName: String = "کاربر",
    val spendingPercent: Float = 0f,
    val savingPercent: Float = 0f,
    val insight: String = "",
    val saving: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,

    /**
     * تعداد حساب
     */
    val walletCount: Int = 0,

    /**
     * تعداد تراکنش
     */
    val transactionCount: Int = 0,

    /**
     * تعداد اعضا
     */
    val memberCount: Int = 0,

    /**
     * تعداد اقساط
     */
    val installmentCount: Int = 0,

    /**
     * وضعیت بارگذاری
     */
    val isLoading: Boolean = false,

    /**
     * خطا
     */
    val error: String? = null
)
package ir.siamak.fintrack.domain.report.model

/**
 * مدل حوزه (Domain) برای محاسبات گزارش هر کیف پول.
 */
data class WalletReport(
    val walletId: Long,
    val walletName: String,
    val walletColor: String,
    val currentBalance: Double, // موجودی فعلی ثبت شده در ولت
    val totalIncome: Double,    // مجموع واریزی‌ها در بازه
    val totalExpense: Double,   // مجموع برداشت‌ها در بازه
    val transactionCount: Int
)

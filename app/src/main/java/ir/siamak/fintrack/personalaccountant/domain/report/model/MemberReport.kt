package ir.siamak.fintrack.personalaccountant.domain.model

/**
 * Domain model for member financial report.
 *
 * این مدل، خروجی نهایی گزارش مالی هر عضو را در لایه دامین نشان می‌دهد.
 * داده‌های آن باید بر اساس بازه زمانی انتخاب‌شده محاسبه شوند.
 *
 * @property memberId شناسه عضو
 * @property memberName نام عضو
 * @property totalIncome مجموع درآمد عضو در بازه
 * @property totalExpense مجموع هزینه عضو در بازه
 * @property transactionCount تعداد کل تراکنش‌های عضو در بازه
 */
data class MemberReport(
    val memberId: Long,
    val memberName: String,
    val totalIncome: Long,
    val totalExpense: Long,
    val transactionCount: Int,
) {
    /**
     * مانده نهایی عضو در بازه گزارش.
     */
    val balance: Long
        get() = totalIncome - totalExpense
}


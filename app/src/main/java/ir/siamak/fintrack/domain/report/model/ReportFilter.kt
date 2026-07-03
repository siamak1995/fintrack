package ir.siamak.fintrack.domain.report.model

data class ReportFilter(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val memberId: Long? = null,
    val walletId: Long? = null,
    val includeIncome: Boolean = true,
    val includeExpense: Boolean = true
)
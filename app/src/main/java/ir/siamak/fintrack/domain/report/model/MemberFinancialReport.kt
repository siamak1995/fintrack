package ir.siamak.fintrack.domain.report.model

data class MemberFinancialReport(
    val memberId: Long,
    val memberName: String,
    val income: Double,
    val expense: Double,
    val balance: Double
)
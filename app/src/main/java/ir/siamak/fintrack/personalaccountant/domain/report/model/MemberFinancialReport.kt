package ir.siamak.fintrack.personalaccountant.domain.report.model

data class MemberFinancialReport(
    val memberId: Long,
    val memberName: String,
    val income: Double,
    val expense: Double,
    val balance: Double
)

package ir.siamak.fintrack.domain.report.model

data class AdvancedReport(
    val income: Double,
    val expense: Double,
    val balance: Double,
    val byMember: List<MemberFinancialReport>
)


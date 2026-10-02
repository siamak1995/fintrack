package ir.siamak.fintrack.personalaccountant.domain.report.model

data class AdvancedReport(
    val income: Double,
    val expense: Double,
    val balance: Double,
    val byMember: List<MemberFinancialReport>
)



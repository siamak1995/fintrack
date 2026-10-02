package ir.siamak.fintrack.personalaccountant.presentation.report.pages.member

data class MemberReportItemUiModel(
    val memberId: Long,
    val memberName: String,
    val totalIncome: Long,
    val totalExpense: Long,
) {
    val balance: Long
        get() = totalIncome - totalExpense
}


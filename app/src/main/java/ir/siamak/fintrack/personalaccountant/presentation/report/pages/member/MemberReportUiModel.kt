package ir.siamak.fintrack.personalaccountant.presentation.report.pages.member

import ir.siamak.fintrack.personalaccountant.domain.model.MemberReport

/**
 * UI model for displaying member report data.
 *
 * این مدل برای نمایش داده‌ها در کارت‌های رابط کاربری استفاده می‌شود.
 *
 * @property memberId شناسه عضو
 * @property memberName نام عضو
 * @property totalIncome مجموع درآمد
 * @property totalExpense مجموع هزینه
 * @property transactionCount تعداد تراکنش‌ها
 */
data class MemberReportUiModel(
    val memberId: Long,
    val memberName: String,
    val totalIncome: Long,
    val totalExpense: Long,
    val transactionCount: Int,
) {
    val balance: Long
        get() = totalIncome - totalExpense
}

/**
 * Maps domain model to UI model.
 */
fun MemberReport.toUiModel(): MemberReportUiModel {
    return MemberReportUiModel(
        memberId = memberId,
        memberName = memberName,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        transactionCount = transactionCount
    )
}


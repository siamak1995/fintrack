package ir.siamak.fintrack.personalaccountant.domain.report

data class MonthlyReportItem(

    val month: String,

    val income: Double,

    val expense: Double,

    val saving: Double

)

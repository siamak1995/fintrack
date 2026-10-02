package ir.siamak.fintrack.personalaccountant.presentation.report.pages.visual

data class BarChartData(
    val label: String,
    val value: Float,
    val secondaryValue: Float? = null // برای مقایسه با دوره قبل
)


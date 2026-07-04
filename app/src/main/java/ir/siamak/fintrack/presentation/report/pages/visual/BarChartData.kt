package ir.siamak.fintrack.presentation.report.pages.visual

data class BarChartData(
    val label: String,
    val value: Float,
    val secondaryValue: Float? = null // برای مقایسه با دوره قبل
)

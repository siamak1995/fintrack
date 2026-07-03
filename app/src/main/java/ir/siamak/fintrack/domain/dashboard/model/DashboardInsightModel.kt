package ir.siamak.fintrack.domain.dashboard.model

data class DashboardInsightModel(

    val title: String,

    val description: String,

    val level: InsightLevel

)

enum class InsightLevel {

    GOOD,

    WARNING,

    DANGER

}
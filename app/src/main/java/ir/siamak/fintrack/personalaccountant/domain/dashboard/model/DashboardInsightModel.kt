package ir.siamak.fintrack.personalaccountant.domain.dashboard.model

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

package ir.siamak.fintrack.domain.dashboard.model


data class DashboardSummaryModel(

    val totalBalance: Double,

    val walletBalance: Double,

    val income: Double,

    val expense: Double,

    val saving: Double

)
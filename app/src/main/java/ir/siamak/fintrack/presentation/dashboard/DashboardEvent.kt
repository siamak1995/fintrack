package ir.siamak.fintrack.presentation.dashboard

/**
 * Eventهای داشبورد
 */
sealed interface DashboardEvent {

    /**
     * بارگذاری مجدد اطلاعات
     */
    data object RefreshData : DashboardEvent

    data object RefreshChart : DashboardEvent

    data object RefreshInsight : DashboardEvent

}
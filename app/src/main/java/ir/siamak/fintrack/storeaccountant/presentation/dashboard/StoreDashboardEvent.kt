package ir.siamak.fintrack.storeaccountant.presentation.dashboard

/**
 * رویدادهای صفحه داشبورد فروشگاه.
 */
sealed class StoreDashboardEvent {
    object Refresh : StoreDashboardEvent()
}


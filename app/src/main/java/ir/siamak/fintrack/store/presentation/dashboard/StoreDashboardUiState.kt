package ir.siamak.fintrack.store.presentation.dashboard

/** Immutable state for the store dashboard. */
data class StoreDashboardUiState(val isLoading: Boolean = false, val todaySales: Long = 0, val invoiceCount: Int = 0, val recentSaleCount: Int = 0, val error: String? = null)

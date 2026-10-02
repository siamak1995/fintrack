package ir.siamak.fintrack.store.presentation.sales.list

/** Immutable state for the sales overview. */
data class SalesUiState(val isLoading: Boolean = false, val saleCount: Int = 0, val salesAmount: Long = 0, val error: String? = null)

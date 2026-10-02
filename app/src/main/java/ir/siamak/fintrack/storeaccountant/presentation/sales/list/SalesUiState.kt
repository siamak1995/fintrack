package ir.siamak.fintrack.storeaccountant.presentation.sales.list

import ir.siamak.fintrack.storeaccountant.domain.model.Sale

data class SalesUiState(
    val sales: List<Sale> = emptyList(),
    val todaySalesCount: Int = 0,
    val todaySalesAmount: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

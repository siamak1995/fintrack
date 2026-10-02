package ir.siamak.fintrack.storeaccountant.presentation.dashboard

/**
 * وضعیت نمایشی صفحه داشبورد فروشگاه.
 */
data class StoreDashboardUiState(
    val isLoading: Boolean = false,
    val storeName: String = "نام فروشگاه",
    val todaySales: Long = 0,
    val invoiceCount: Int = 0,
    val totalRevenue: Long = 0,
    val error: String? = null
)


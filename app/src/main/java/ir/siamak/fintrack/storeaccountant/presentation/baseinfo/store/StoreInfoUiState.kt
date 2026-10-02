package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.store

data class StoreInfoUiState(
    val id: Long = 0,
    val name: String = "",
    val brand: String = "",
    val phone: String = "",
    val address: String = "",
    val description: String = "",
    val logoPath: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

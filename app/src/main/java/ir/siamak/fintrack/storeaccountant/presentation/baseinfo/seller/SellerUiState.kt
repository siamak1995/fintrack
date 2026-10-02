package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller

import ir.siamak.fintrack.storeaccountant.domain.model.Seller

data class SellerUiState(
    val sellers: List<Seller> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // For Add/Edit
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val address: String = "",
    val description: String = "",
    val isSaved: Boolean = false
)

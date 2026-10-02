package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.material

import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial

data class MaterialUiState(
    val materials: List<RawMaterial> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // For Add/Edit
    val name: String = "",
    val unit: String = "",
    val pricePerUnit: String = "",
    val quantity: String = "",
    val purchaseDate: String = "",
    val shippingCost: String = "",
    val isSaved: Boolean = false
)

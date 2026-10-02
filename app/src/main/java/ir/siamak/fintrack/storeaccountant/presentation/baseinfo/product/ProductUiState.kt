package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.product

import ir.siamak.fintrack.storeaccountant.domain.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // For Add/Edit
    val name: String = "",
    val material: String = "",
    val size: String = "",
    val weight: String = "",
    val stock: String = "",
    val price: String = "",
    val isSaved: Boolean = false
)

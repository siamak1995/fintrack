package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** Immutable product quantity snapshot belonging to a sale. */
@Serializable
data class SaleItem(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Long
)

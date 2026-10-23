package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** A raw-material requirement of a custom pre-order. */
@Serializable
data class PreOrderItem(
    val rawMaterialId: Long,
    val materialName: String,
    val quantity: Double,
    val unitPrice: Long
)

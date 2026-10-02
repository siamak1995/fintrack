package ir.siamak.fintrack.store.domain.model

/** A raw-material requirement of a custom pre-order. */
data class PreOrderItem(
    val rawMaterialId: Long,
    val materialName: String,
    val quantity: Double,
    val unitPrice: Long
)

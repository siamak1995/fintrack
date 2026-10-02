package ir.siamak.fintrack.store.domain.model

/** A stock-controlled raw material used to make custom orders. */
data class RawMaterial(
    val id: Long = 0,
    val name: String,
    val unit: String,
    val pricePerUnit: Long,
    val quantity: Double = 0.0,
    val purchaseDate: String? = null,
    val shippingCost: Long? = null
)

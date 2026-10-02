package ir.siamak.fintrack.storeaccountant.domain.model

data class RawMaterial(
    val id: Long,
    val name: String,
    val unit: String,
    val pricePerUnit: Long,
    val quantity: Double,
    val purchaseDate: String?,
    val shippingCost: Long?
)

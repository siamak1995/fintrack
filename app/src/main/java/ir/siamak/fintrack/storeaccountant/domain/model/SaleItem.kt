package ir.siamak.fintrack.storeaccountant.domain.model

data class SaleItem(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Long
)

package ir.siamak.fintrack.store.domain.model

/** Immutable product quantity snapshot belonging to a sale. */
data class SaleItem(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Long
)

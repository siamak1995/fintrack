package ir.siamak.fintrack.store.domain.model

/** A completed store sale. Monetary totals are calculated by use cases. */
data class Sale(
    val id: Long = 0,
    val customerId: Long,
    val sellerId: Long? = null,
    val items: List<SaleItem>,
    val discount: Discount? = null,
    val tax: Tax = Tax(),
    val payment: Payment,
    val subtotal: Long,
    val discountAmount: Long,
    val taxAmount: Long,
    val finalAmount: Long,
    val createdAt: String
)

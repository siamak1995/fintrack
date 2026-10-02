package ir.siamak.fintrack.store.domain.model

/** Renderable invoice snapshot for a sale or pre-order. */
data class Invoice(
    val id: Long = 0,
    val invoiceNumber: String,
    val date: String,
    val store: Store,
    val seller: Seller? = null,
    val customer: Customer,
    val items: List<SaleItem> = emptyList(),
    val discountAmount: Long = 0,
    val taxAmount: Long = 0,
    val payment: Payment? = null,
    val finalAmount: Long,
    val paymentStatus: PaymentStatus
) {
    enum class PaymentStatus { SETTLED, UNSETTLED, INSTALLMENT }
}

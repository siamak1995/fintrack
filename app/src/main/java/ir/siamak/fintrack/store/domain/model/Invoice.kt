package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** Renderable invoice snapshot for a sale or pre-order. */
@Serializable
data class Invoice(
    val id: Long = 0,
    val invoiceNumber: String,
    val date: String,
    val store: Store,
    val seller: Seller? = null,
    val customer: Customer,
    val items: List<SaleItem> = emptyList(),
    val discount: Discount? = null,
    val tax: Tax = Tax(),
    val pricing: PreOrderPricing? = null,
    val discountAmount: Long = 0,
    val taxAmount: Long = 0,
    val payment: Payment? = null,
    val finalAmount: Long,
    val description: String? = null,
    val deliveryDate: String? = null,
    val preOrderId: Long? = null,
    val materialItems: List<PreOrderItem> = emptyList(),
    val requestId: String = "",
    val cancelled: Boolean = false,
    val paymentStatus: PaymentStatus,
    val saleId: Long? = null,
    val displaySequence: Long = 0,
    val settlement: StoreSettlementAccount? = null,
    val walletPostedAmount: Long = 0
) {
    enum class PaymentStatus { SETTLED, UNSETTLED, INSTALLMENT }
}

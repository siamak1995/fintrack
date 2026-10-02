package ir.siamak.fintrack.store.domain.model

/** A custom order that moves through production and delivery states. */
data class PreOrder(
    val id: Long = 0,
    val customerId: Long,
    val description: String,
    val deliveryDate: String,
    val agreedPrice: Long,
    val shippingCost: Long = 0,
    val status: PreOrderStatus = PreOrderStatus.NEW,
    val items: List<PreOrderItem> = emptyList(),
    val advancePayment: Long = 0
)

/** Lifecycle states for a custom pre-order. */
enum class PreOrderStatus { NEW, IN_PROGRESS, READY, DELIVERED, CANCELLED }

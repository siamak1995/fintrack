package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** A custom order that moves through production and delivery states. */
@Serializable
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

package ir.siamak.fintrack.store.domain.model
import kotlinx.serialization.Serializable
@Serializable data class PreOrderPricing(
    val materialCost: Long, val productionCost: Long, val profit: Long, val shippingCost: Long, val suggestedPrice: Long
)

package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** Discount supplied when a sale is finalized. */
@Serializable
data class Discount(val type: DiscountType, val value: Long) {
    enum class DiscountType { PERCENT, FIXED }
}

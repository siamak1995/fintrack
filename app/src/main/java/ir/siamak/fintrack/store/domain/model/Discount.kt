package ir.siamak.fintrack.store.domain.model

/** Discount supplied when a sale is finalized. */
data class Discount(val type: DiscountType, val value: Long) {
    enum class DiscountType { PERCENT, FIXED }
}

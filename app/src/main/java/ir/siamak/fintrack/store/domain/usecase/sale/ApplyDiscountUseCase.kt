package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.Discount

/** Calculates a bounded discount amount for a subtotal. */
class ApplyDiscountUseCase {
    operator fun invoke(subtotal: Long, discount: Discount?): Long {
        require(subtotal >= 0) { "Subtotal cannot be negative" }
        if (discount == null) return 0
        require(discount.value >= 0) { "Discount cannot be negative" }
        val amount = when (discount.type) {
            Discount.DiscountType.PERCENT -> {
                require(discount.value <= PERCENT_BASE) { "Percentage must be at most 100" }
                subtotal * discount.value / PERCENT_BASE
            }
            Discount.DiscountType.FIXED -> discount.value
        }
        return amount.coerceAtMost(subtotal)
    }

    private companion object { const val PERCENT_BASE = 100L }
}

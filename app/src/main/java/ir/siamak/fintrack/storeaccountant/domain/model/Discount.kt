package ir.siamak.fintrack.storeaccountant.domain.model

enum class DiscountType {
    PERCENT, FIXED
}

data class Discount(
    val type: DiscountType,
    val value: Long
)

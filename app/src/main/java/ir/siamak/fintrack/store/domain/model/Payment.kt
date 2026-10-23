package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** How the customer settles a sale or pre-order. */
@Serializable
data class Payment(
    val type: PaymentType,
    val paidAmount: Long,
    val installments: List<Installment> = emptyList()
) {
    enum class PaymentType { CASH, INSTALLMENT }
}

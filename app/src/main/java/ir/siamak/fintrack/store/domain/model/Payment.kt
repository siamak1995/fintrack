package ir.siamak.fintrack.store.domain.model

/** How the customer settles a sale or pre-order. */
data class Payment(
    val type: PaymentType,
    val paidAmount: Long,
    val installments: List<Installment> = emptyList()
) {
    enum class PaymentType { CASH, INSTALLMENT }
}

package ir.siamak.fintrack.store.domain.model

/** One scheduled payment in an installment plan. */
data class Installment(
    val monthNumber: Int,
    val percentage: Int,
    val amount: Long,
    val dueDate: String
)

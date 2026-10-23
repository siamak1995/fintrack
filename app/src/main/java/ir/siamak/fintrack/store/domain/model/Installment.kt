package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** One scheduled payment in an installment plan. */
@Serializable
data class Installment(
    val monthNumber: Int,
    val percentage: Int,
    val amount: Long,
    val dueDate: String
)

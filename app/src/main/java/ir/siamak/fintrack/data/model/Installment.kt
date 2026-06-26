package ir.siamak.fintrack.data.model

data class Installment(
    val id: Long,
    val title: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val dueDate: Long
)

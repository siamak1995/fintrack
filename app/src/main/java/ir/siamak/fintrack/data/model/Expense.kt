package ir.siamak.fintrack.data.model

data class Expense(
    val id: Long,
    val walletId: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long
)

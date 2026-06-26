package ir.siamak.fintrack.data.model

enum class TransactionType { INCOME, EXPENSE, TRANSFER }

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val walletId: Long,
    val memberId: Long,
    val date: Long,
    val note: String? = null
)

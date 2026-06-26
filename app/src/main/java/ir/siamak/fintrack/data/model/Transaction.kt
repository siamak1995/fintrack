package ir.siamak.fintrack.data.model

import java.util.Date

enum class TransactionType { INCOME, EXPENSE, TRANSFER }

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val walletId: Long,
    val memberId: Long,
    val date: Date,
    val note: String? = null
)

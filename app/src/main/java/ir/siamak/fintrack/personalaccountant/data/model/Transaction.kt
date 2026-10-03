package ir.siamak.fintrack.personalaccountant.data.model

import ir.siamak.fintrack.personalaccountant.domain.context.LEGACY_PERSONAL_CONTEXT_ID

enum class TransactionType { INCOME, EXPENSE, TRANSFER }

data class Transaction(
    val id: Long = 0,
    val contextId: Long = LEGACY_PERSONAL_CONTEXT_ID,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val walletId: Long,
    val toWalletId: Long? = null,
    val memberId: Long,
    val date: Long,
    val note: String,
    val tags: List<Tag> = emptyList()
)

fun TransactionType.toPersian(): String = when(this) {
    TransactionType.INCOME -> "درآمد"
    TransactionType.EXPENSE -> "هزینه"
    TransactionType.TRANSFER -> "انتقال"
}


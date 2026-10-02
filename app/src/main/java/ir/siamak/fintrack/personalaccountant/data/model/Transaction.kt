package ir.siamak.fintrack.personalaccountant.data.model

enum class TransactionType { INCOME, EXPENSE, TRANSFER }

data class Transaction(
    val id: Long = 0,
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


package ir.siamak.fintrack.presentation.add_edit_transaction

/**
 * وضعیت فیلدهای صفحه افزودن تراکنش.
 */
data class AddEditTransactionState(
    val description: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val date: Long = System.currentTimeMillis(),
    val walletId: Long? = null,
    val isSaving: Boolean = false
)

enum class TransactionType { INCOME, EXPENSE }

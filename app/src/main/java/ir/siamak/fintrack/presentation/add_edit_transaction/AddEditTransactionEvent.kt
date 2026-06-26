package ir.siamak.fintrack.presentation.add_edit_transaction

sealed class AddEditTransactionEvent {
    data class EnteredDescription(val value: String): AddEditTransactionEvent()
    data class EnteredAmount(val value: String): AddEditTransactionEvent()
    data class SelectType(val type: TransactionType): AddEditTransactionEvent()
    object SaveTransaction: AddEditTransactionEvent()
}

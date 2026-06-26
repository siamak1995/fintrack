package ir.siamak.fintrack.presentation.transaction.add_edit_transaction


import ir.siamak.fintrack.data.model.TransactionType

sealed class AddEditTransactionEvent {
    data class EnteredAmount(val value: String) : AddEditTransactionEvent()
    data class TypeChanged(val type: TransactionType) : AddEditTransactionEvent()
    data class CategorySelected(val categoryName: String) : AddEditTransactionEvent()
    data class WalletSelected(val walletId: Long) : AddEditTransactionEvent()
    data class EnteredNote(val value: String) : AddEditTransactionEvent()
    object SaveTransaction : AddEditTransactionEvent()
}

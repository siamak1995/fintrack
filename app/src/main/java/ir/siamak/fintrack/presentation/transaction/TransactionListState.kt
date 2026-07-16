package ir.siamak.fintrack.presentation.transaction


import ir.siamak.fintrack.data.model.Transaction

data class TransactionListState(
    val transactions: List<Transaction> = emptyList()
)
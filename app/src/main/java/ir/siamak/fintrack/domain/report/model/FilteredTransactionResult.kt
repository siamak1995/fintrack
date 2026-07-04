package ir.siamak.fintrack.domain.report.model

import ir.siamak.fintrack.data.model.Transaction

data class FilteredTransactionResult(
    val transaction: Transaction,
    val walletName: String,
    val walletColor: String,
    val memberName: String,
    val memberColor: String
)

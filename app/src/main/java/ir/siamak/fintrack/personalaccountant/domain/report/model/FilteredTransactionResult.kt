package ir.siamak.fintrack.personalaccountant.domain.report.model

import ir.siamak.fintrack.personalaccountant.data.model.Transaction

data class FilteredTransactionResult(
    val transaction: Transaction,
    val walletName: String,
    val walletColor: String,
    val memberName: String,
    val memberColor: String
)


package ir.siamak.fintrack.personalaccountant.presentation.report.pages.wallet

import ir.siamak.fintrack.personalaccountant.domain.report.model.WalletReport

data class WalletReportUiModel(
    val walletId: Long,
    val walletName: String,
    val walletColor: String,
    val currentBalance: Long,
    val totalIncome: Long,
    val totalExpense: Long,
    val transactionCount: Int
) {
    // تراز بازه انتخابی (خالص عملکرد)
    val balanceDifference: Long
        get() = totalIncome - totalExpense
}

fun WalletReport.toUiModel(): WalletReportUiModel {
    return WalletReportUiModel(
        walletId = walletId,
        walletName = walletName,
        walletColor = walletColor,
        currentBalance = currentBalance.toLong(),
        totalIncome = totalIncome.toLong(),
        totalExpense = totalExpense.toLong(),
        transactionCount = transactionCount
    )
}


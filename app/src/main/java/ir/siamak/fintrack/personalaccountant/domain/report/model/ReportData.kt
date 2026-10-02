package ir.siamak.fintrack.personalaccountant.domain.report.model

import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.Wallet

data class ReportData(

    val transactions: List<Transaction>,

    val members: List<Member>,

    val wallets: List<Wallet>

)

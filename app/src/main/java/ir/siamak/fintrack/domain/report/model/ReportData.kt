package ir.siamak.fintrack.domain.report.model

import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet

data class ReportData(

    val transactions: List<Transaction>,

    val members: List<Member>,

    val wallets: List<Wallet>

)
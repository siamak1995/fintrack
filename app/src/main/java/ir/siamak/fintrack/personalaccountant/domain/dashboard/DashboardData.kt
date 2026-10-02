package ir.siamak.fintrack.personalaccountant.domain.dashboard

import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.Wallet

/**
 * تمام داده‌های موردنیاز داشبورد.
 */
data class DashboardData(

    val wallets: List<Wallet>,

    val transactions: List<Transaction>,

    val members: List<Member>,

    val installments: List<Installment>

)

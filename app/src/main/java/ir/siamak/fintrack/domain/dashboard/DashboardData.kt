package ir.siamak.fintrack.domain.dashboard

import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet

/**
 * تمام داده‌های موردنیاز داشبورد.
 */
data class DashboardData(

    val wallets: List<Wallet>,

    val transactions: List<Transaction>,

    val members: List<Member>,

    val installments: List<Installment>

)
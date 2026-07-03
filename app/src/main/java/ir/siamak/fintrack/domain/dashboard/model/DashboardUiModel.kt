package ir.siamak.fintrack.domain.dashboard.model

import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet

data class DashboardUiModel(

    val wallets: List<Wallet>,

    val recentTransactions: List<Transaction>,

    val members: List<Member>,

    val installments: List<Installment>,

    val income: Double,

    val expense: Double,

    val balance: Double,

    val saving: Double,

    val spendingPercent: Float,

    val savingPercent: Float,

    val walletBalance: Double,

    val todayIncome: Double,

    val todayExpense: Double,

    val insight: String

)
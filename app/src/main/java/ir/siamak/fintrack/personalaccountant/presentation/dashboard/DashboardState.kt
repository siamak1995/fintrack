package ir.siamak.fintrack.personalaccountant.presentation.dashboard

import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.Wallet

data class DashboardState(

    val wallets:List<Wallet> = emptyList(),

    val members:List<Member> = emptyList(),

    val transactions:List<Transaction> = emptyList(),

    val recentTransactions:List<Transaction> = emptyList(),

    val installments:List<Installment> = emptyList(),

    val upcomingInstallments:List<Installment> = emptyList(),

    val monthlyIncome:Double = 0.0,

    val monthlyExpense:Double = 0.0,

    val totalBalance:Double = 0.0,

    val walletBalance:Double = 0.0,

    val saving:Double = 0.0,

    val todayIncome:Double = 0.0,

    val todayExpense:Double = 0.0,

    val spendingPercent:Float = 0f,

    val savingPercent:Float = 0f,

    val walletCount:Int = 0,

    val memberCount:Int = 0,

    val transactionCount:Int = 0,

    val installmentCount:Int = 0,

    val financialHealth:FinancialHealth = FinancialHealth.GOOD,

    val biggestExpense:Transaction? = null,

    val averageDailyExpense:Double = 0.0,

    val insight:String = "",

    val userName:String = "کاربر",

    val isLoading:Boolean = false,

    val error:String? = null

)

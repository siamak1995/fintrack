package ir.siamak.fintrack.presentation.report

import ir.siamak.fintrack.domain.report.CategoryReportItem
import ir.siamak.fintrack.domain.report.MonthlyReportItem
import ir.siamak.fintrack.domain.report.WalletReportItem

data class ReportsState(

    val totalIncome: Double = 0.0,

    val totalExpense: Double = 0.0,

    val totalSaving: Double = 0.0,

    val monthlyReports: List<MonthlyReportItem> = emptyList(),

    val walletReports: List<WalletReportItem> = emptyList(),

    val categoryReports: List<CategoryReportItem> = emptyList(),

    val isLoading: Boolean = false,

    val error: String? = null

)
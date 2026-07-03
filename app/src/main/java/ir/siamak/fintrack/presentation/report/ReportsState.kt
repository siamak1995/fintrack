package ir.siamak.fintrack.presentation.report

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.report.CategoryReportItem
import ir.siamak.fintrack.domain.report.MonthlyReportItem
import ir.siamak.fintrack.domain.report.WalletReportItem
import ir.siamak.fintrack.domain.report.model.AdvancedReport
import ir.siamak.fintrack.domain.report.model.MemberFinancialReport
import ir.siamak.fintrack.domain.report.model.ReportFilter

data class ReportsState(

    // --- FILTER SYSTEM ---
    val filter: ReportFilter = ReportFilter(),

    // --- ADVANCED REPORT ---
    val report: AdvancedReport? = null,
    val memberReports: List<MemberFinancialReport> = emptyList(),

    // --- LEGACY REPORT UI (UI فعلی تو) ---
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalSaving: Double = 0.0,

    val monthlyReports: List<MonthlyReportItem> = emptyList(),
    val walletReports: List<WalletReportItem> = emptyList(),
    val categoryReports: List<CategoryReportItem> = emptyList(),
    val transactions: List<Transaction> = emptyList(),

    // --- STATE ---
    val isLoading: Boolean = false,
    val error: String? = null,
)
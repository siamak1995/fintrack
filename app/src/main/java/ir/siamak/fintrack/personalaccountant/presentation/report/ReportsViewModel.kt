package ir.siamak.fintrack.personalaccountant.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.analytics.ReportCalculator
import ir.siamak.fintrack.personalaccountant.domain.report.model.ReportData
import ir.siamak.fintrack.personalaccountant.domain.report.model.ReportFilter
import ir.siamak.fintrack.personalaccountant.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.personalaccountant.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.personalaccountant.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val calculator: ReportCalculator,
    private val transactionUseCases: TransactionUseCases,
    private val memberUseCases: MemberUseCases,
    private val walletUseCases: WalletUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state = _state.asStateFlow()

    private var allTransactions = listOf<Transaction>()
    private var allMembers = listOf<Member>()
    private var allWallets = listOf<Wallet>()

    init {
        loadData()
    }

    private fun loadData() {

        viewModelScope.launch {

            combine(
                transactionUseCases.getAllTransactions(),
                memberUseCases.getAllMembers(),
                walletUseCases.getAllWallets()
            ) { t, m, w ->
                ReportData(
                    transactions = t,
                    members = m,
                    wallets = w
                )
            }.collect { data ->

                allTransactions = data.transactions
                allMembers = data.members
                allWallets = data.wallets

                refreshReport()
            }
        }
    }

    fun updateFilter(filter: ReportFilter) {
        _state.update { it.copy(filter = filter) }
        refreshReport()
    }

    private fun refreshReport() {

        val filter = _state.value.filter
        val filteredTransactions =

            calculator.filterTransactions(

                allTransactions,

                filter

            )

        val filtered =
            calculator.filterTransactions(
                allTransactions,
                filter
            )

        val report =
            calculator.buildAdvancedReport(
                transactions = allTransactions,
                members = allMembers,
                filter = filter
            )

        val memberReports =
            calculator.reportByMember(
                transactions = allTransactions,
                members = allMembers,
                filter = filter
            )

        val monthlyReports =
            calculator.monthlyReport(filtered)

        val walletReports =
            calculator.walletReport(allWallets)

        val categoryReports =
            calculator.categoryReport(filtered)

        _state.update {

            it.copy(

                report = report,

                memberReports = memberReports,

                totalIncome = report.income,

                totalExpense = report.expense,

                totalSaving = report.balance,

                monthlyReports = monthlyReports,

                walletReports = walletReports,

                categoryReports = categoryReports,

                transactions = filteredTransactions,

                isLoading = false,

                error = null

            )

        }

    }
}

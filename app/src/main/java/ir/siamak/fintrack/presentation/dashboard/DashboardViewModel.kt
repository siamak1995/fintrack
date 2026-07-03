package ir.siamak.fintrack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.analytics.DashboardCalculator
import ir.siamak.fintrack.domain.dashboard.DashboardData
import ir.siamak.fintrack.domain.usecase.installments.InstallmentUseCases
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val walletUseCases: WalletUseCases,
    private val transactionUseCases: TransactionUseCases,
    private val memberUseCases: MemberUseCases,
    private val installmentUseCases: InstallmentUseCases,
    private val calculator: DashboardCalculator

) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())

    val state = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        onEvent(DashboardEvent.RefreshData)
    }

    fun onEvent(event: DashboardEvent) {

        when(event){

            DashboardEvent.RefreshData -> loadDashboard()
            DashboardEvent.RefreshChart -> refreshChart()
            DashboardEvent.RefreshInsight -> refreshInsight()

        }

    }

    private fun loadDashboard() {

        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            _state.update {

                it.copy(
                    isLoading = true,
                    error = null
                )

            }

            combine(

                walletUseCases.getAllWallets(),

                transactionUseCases.getAllTransactions(),

                memberUseCases.getAllMembers(),

                installmentUseCases.getAllInstallments()

            ){ wallets,
               transactions,
               members,
               installments ->

                DashboardData(

                    wallets = wallets,

                    transactions = transactions,

                    members = members,

                    installments = installments

                )


            }
                .catch { throwable ->

                    _state.update { state ->

                        state.copy(
                            isLoading = false,
                            error = throwable.message ?: "خطایی در بارگذاری اطلاعات رخ داد."
                        )

                    }

                }
                .collect { data ->

                    val money = calculator.calculateMoney(

                        data.wallets,

                        data.transactions

                    )

                    val chart = calculator.calculateChart(

                        data.transactions

                    )

                    val statistics = calculator.calculateStatistics(

                        data.wallets,

                        data.members,

                        data.installments,

                        data.transactions

                    )

                    _state.update {

                        it.copy(

                            wallets = data.wallets,

                            transactions = data.transactions,

                            members = data.members,

                            installments = data.installments,

                            recentTransactions =
                                calculator.recentTransactions(data.transactions),

                            monthlyIncome = money.income,

                            monthlyExpense = money.expense,

                            totalBalance = money.balance,

                            saving = money.saving,

                            walletBalance = money.walletBalance,

                            todayIncome = money.todayIncome,

                            todayExpense = money.todayExpense,

                            spendingPercent = chart.spendingPercent,

                            savingPercent = chart.savingPercent,

                            walletCount = statistics.walletCount,

                            transactionCount = statistics.transactionCount,

                            memberCount = statistics.memberCount,

                            installmentCount = statistics.installmentCount,

                            insight = calculator.insight(data.transactions),

                            isLoading = false,

                            error = null

                        )

                    }

                }
        }

    }

    private fun refreshChart(){

    }

    private fun refreshInsight(){

    }
}

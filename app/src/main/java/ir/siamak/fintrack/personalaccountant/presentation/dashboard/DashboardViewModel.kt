package ir.siamak.fintrack.personalaccountant.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.domain.analytics.DashboardCalculator
import ir.siamak.fintrack.personalaccountant.domain.dashboard.DashboardData
import ir.siamak.fintrack.personalaccountant.domain.usecase.installments.InstallmentUseCases
import ir.siamak.fintrack.personalaccountant.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.personalaccountant.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.personalaccountant.domain.usecase.wallet.WalletUseCases
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

    private val _state=MutableStateFlow(DashboardState())
    val state=_state.asStateFlow()

    private var loadJob:Job?=null

    init{
        loadDashboard()
    }

    fun onEvent(event:DashboardEvent){
        when(event){
            DashboardEvent.RefreshData->loadDashboard()
            DashboardEvent.RefreshChart->refreshChart()
            DashboardEvent.RefreshInsight->refreshInsight()
        }
    }

    private fun loadDashboard(){

        loadJob?.cancel()

        loadJob=viewModelScope.launch {

            _state.update{
                it.copy(
                    isLoading=true,
                    error=null
                )
            }

            combine(
                walletUseCases.getAllWallets(),
                transactionUseCases.getAllTransactions(),
                memberUseCases.getAllMembers(),
                installmentUseCases.getAllInstallments()
            ){wallets,transactions,members,installments->

                DashboardData(
                    wallets=wallets,
                    transactions=transactions,
                    members=members,
                    installments=installments
                )

            }.catch { e->

                _state.update{
                    it.copy(
                        isLoading=false,
                        error=e.message?:"خطا در دریافت اطلاعات"
                    )
                }

            }.collect{data->

                val money=calculator.calculateMonthlyMoney(
                    wallets=data.wallets,
                    transactions=data.transactions
                )

                val chart=calculator.calculateChart(
                    data.transactions
                )

                val statistics=calculator.calculateStatistics(
                    wallets=data.wallets,
                    members=data.members,
                    installments=data.installments,
                    transactions=data.transactions
                )

                _state.update{

                    it.copy(

                        wallets=data.wallets,
                        transactions=data.transactions,
                        recentTransactions=calculator.recentTransactions(data.transactions),

                        members=data.members,

                        installments=data.installments,
                        upcomingInstallments=calculator.upcomingInstallments(data.installments),

                        monthlyIncome=money.income,
                        monthlyExpense=money.expense,
                        totalBalance=money.balance,
                        walletBalance=money.walletBalance,
                        saving=money.saving,

                        todayIncome=money.todayIncome,
                        todayExpense=money.todayExpense,

                        spendingPercent=chart.spendingPercent,
                        savingPercent=chart.savingPercent,

                        walletCount=statistics.walletCount,
                        transactionCount=statistics.transactionCount,
                        memberCount=statistics.memberCount,
                        installmentCount=statistics.installmentCount,

                        insight=calculator.generateInsight(
                            transactions=data.transactions,
                            installments=data.installments
                        ),

                        financialHealth=calculator.financialHealth(
                            data.transactions
                        ),

                        biggestExpense=calculator.biggestExpense(
                            data.transactions
                        ),

                        averageDailyExpense=calculator.averageDailyExpense(
                            data.transactions
                        ),

                        isLoading=false,
                        error=null
                    )

                }

            }

        }

    }

    private fun refreshChart(){

        val chart=calculator.calculateChart(
            state.value.transactions
        )

        _state.update{
            it.copy(
                savingPercent=chart.savingPercent,
                spendingPercent=chart.spendingPercent
            )
        }

    }

    private fun refreshInsight(){

        _state.update{

            it.copy(

                insight=calculator.generateInsight(
                    transactions=it.transactions,
                    installments=it.installments
                ),

                financialHealth=calculator.financialHealth(
                    it.transactions
                ),

                biggestExpense=calculator.biggestExpense(
                    it.transactions
                ),

                averageDailyExpense=calculator.averageDailyExpense(
                    it.transactions
                )

            )

        }

    }

}

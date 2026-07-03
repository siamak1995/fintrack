package ir.siamak.fintrack.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.analytics.ReportCalculator
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(

    private val walletUseCases: WalletUseCases,

    private val transactionUseCases: TransactionUseCases

) : ViewModel() {

    private val calculator = ReportCalculator()

    private val _state = MutableStateFlow(ReportsState())

    val state = _state.asStateFlow()

    init {

        onEvent(
            ReportsEvent.LoadReports
        )

    }

    fun onEvent(

        event: ReportsEvent

    ) {

        when (event) {

            ReportsEvent.LoadReports,
            ReportsEvent.Refresh ->

                load()

        }

    }

    private fun load() {

        viewModelScope.launch {

            _state.update {

                it.copy(
                    isLoading = true,
                    error = null
                )

            }

            combine(

                walletUseCases.getAllWallets(),

                transactionUseCases.getAllTransactions()

            ) { wallets, transactions ->

                Pair(
                    wallets,
                    transactions
                )

            }
                .catch {

                    _state.update { state ->

                        state.copy(

                            isLoading = false,

                            error = it.message

                        )

                    }

                }
                .collect { (wallets, transactions) ->

                    _state.update {

                        it.copy(

                            totalIncome =
                                calculator.totalIncome(transactions),

                            totalExpense =
                                calculator.totalExpense(transactions),

                            totalSaving =
                                calculator.totalSaving(transactions),

                            monthlyReports =
                                calculator.monthlyReport(transactions),

                            walletReports =
                                calculator.walletReport(wallets),

                            categoryReports =
                                calculator.categoryReport(transactions),

                            isLoading = false

                        )

                    }

                }

        }

    }

}
package ir.siamak.fintrack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.TransactionType
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
    private val transactionUseCases: TransactionUseCases

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

                transactionUseCases.getAllTransactions()

            ){ wallets , transactions ->

                Pair(wallets,transactions)

            }
                .catch { throwable ->

                    _state.update { state ->

                        state.copy(
                            isLoading = false,
                            error = throwable.message ?: "خطایی در بارگذاری اطلاعات رخ داد."
                        )

                    }

                }
                .collect { (wallets,transactions) ->

                    val income = transactions
                        .filter { it.type == TransactionType.INCOME }
                        .sumOf { it.amount }

                    val expense = transactions
                        .filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount }

//                    val balance = wallets.sumOf { it.balance }
                    val balance = income - expense

                    _state.update {

                        it.copy(

                            wallets = wallets,

                            transactions = transactions,

                            totalIncome = income,

                            totalExpense = expense,

                            currentBalance = balance,

                            walletCount = wallets.size,

                            transactionCount = transactions.size,

                            isLoading = false,

                            error = null

                        )

                    }

                }

        }

    }
}

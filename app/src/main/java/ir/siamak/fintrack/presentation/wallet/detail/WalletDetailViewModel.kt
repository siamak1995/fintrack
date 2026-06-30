//package ir.siamak.fintrack.presentation.wallet.detail
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import ir.siamak.fintrack.domain.analytics.WalletCalculator
//import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class WalletDetailViewModel @Inject constructor(
//    private val transactionUseCases: TransactionUseCases,
//    private val walletCalculator: WalletCalculator,
//    savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private val walletId: Long = checkNotNull(savedStateHandle["walletId"])
//
//    private val _uiState = MutableStateFlow(WalletDetailUiState())
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        loadTransactions()
//    }
//
//    private fun loadTransactions() {
//        viewModelScope.launch {
//            transactionUseCases.getTransactionsByWallet(walletId)
//                .collect { transactions ->
//                    val grouped = walletCalculator.groupTransactionsByDate(transactions)
//                    _uiState.update { it.copy(groupedTransactions = grouped) }
//                }
//        }
//    }
//}

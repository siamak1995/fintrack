package ir.siamak.fintrack.presentation.add_edit_transaction

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases,
    private val walletUseCases: WalletUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AddEditTransactionState())
    val state: State<AddEditTransactionState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // گرفتن لیست کیف پول‌ها برای نمایش در Dropdown یا انتخابگر
        viewModelScope.launch {
            walletUseCases.getAllWallets().collect { wallets ->
                _state.value = _state.value.copy(wallets = wallets)
            }
        }
    }

    fun onEvent(event: AddEditTransactionEvent) {
        when (event) {
            is AddEditTransactionEvent.EnteredAmount -> {
                _state.value = _state.value.copy(amount = event.value)
            }
            is AddEditTransactionEvent.TypeChanged -> {
                _state.value = _state.value.copy(type = event.type)
            }
            is AddEditTransactionEvent.CategorySelected -> {
                _state.value = _state.value.copy(selectedCategoryName = event.categoryName)
            }
            is AddEditTransactionEvent.WalletSelected -> {
                _state.value = _state.value.copy(selectedWalletId = event.walletId)
            }
            is AddEditTransactionEvent.EnteredNote -> {
                _state.value = _state.value.copy(note = event.value)
            }
            is AddEditTransactionEvent.SaveTransaction -> {
                saveTransaction()
            }
        }
    }

    private fun saveTransaction() {
        viewModelScope.launch {
            val amount = _state.value.amount.toDoubleOrNull() ?: 0.0
            if (amount <= 0.0 || _state.value.selectedWalletId == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("لطفاً مبلغ و کیف پول را وارد کنید"))
                return@launch
            }

            transactionUseCases.insertTransaction(
                Transaction(
                    amount = amount,
                    type = _state.value.type,
                    categoryName = _state.value.selectedCategoryName,
                    walletId = _state.value.selectedWalletId!!,
                    memberId = 1L, // فعلاً پیش‌فرض برای تست
                    date = System.currentTimeMillis(),
                    note = _state.value.note
                )
            )
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }
}

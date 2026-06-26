package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

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

/**
 * ویومدل برای مدیریت وضعیت و منطق صفحه ثبت/ویرایش تراکنش.
 *
 * @property transactionUseCases یوزکیس‌های مربوط به تراکنش‌ها
 * @property walletUseCases یوزکیس‌های مربوط به حساب‌ها برای بارگذاری لیست کیف‌پول‌ها
 */
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
        // بارگذاری لیست حساب‌های موجود برای انتخاب توسط کاربر
        loadWallets()
    }

    private fun loadWallets() {
        viewModelScope.launch {
            walletUseCases.getAllWallets().collect { wallets ->
                _state.value = _state.value.copy(
                    wallets = wallets,
                    // اگر حسابی موجود است، اولین حساب را به صورت پیش‌فرض انتخاب کن
                    selectedWalletId = if (_state.value.selectedWalletId == -1L && wallets.isNotEmpty())
                        wallets.first().id else _state.value.selectedWalletId
                )
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

            // چک کردن برای null یا مقدار پیش‌فرض -1
            if (amount <= 0.0 || _state.value.selectedWalletId == null || _state.value.selectedWalletId == -1L) {
                _eventFlow.emit(UiEvent.ShowSnackbar("لطفاً مبلغ و حساب بانکی را انتخاب کنید"))
                return@launch
            }

            try {
                transactionUseCases.insertTransaction(
                    Transaction(
                        amount = amount,
                        type = _state.value.type,
                        categoryName = _state.value.selectedCategoryName ?: "عمومی",
                        walletId = _state.value.selectedWalletId!!, // <--- اضافه کردن علامت !! برای رفع خطا
                        memberId = 1L,
                        date = System.currentTimeMillis(),
                        note = _state.value.note
                    )
                )
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("خطا در ذخیره: ${e.message}"))
            }
        }
    }


    /**
     * رویدادهای یک‌باره UI (مانند نمایش اسنک‌بار یا خروج از صفحه)
     */
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }
}

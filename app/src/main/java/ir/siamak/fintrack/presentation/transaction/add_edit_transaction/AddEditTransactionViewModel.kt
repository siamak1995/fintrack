package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.extensions.formatAmount
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
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
    private val walletUseCases: WalletUseCases,
    private val memberUseCases: MemberUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AddEditTransactionState())
    val state: State<AddEditTransactionState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // بارگذاری لیست حساب‌های موجود برای انتخاب توسط کاربر
        loadWallets()
        // بارگذاری لیست اعضا موجود برای انتخاب توسط کاربر
        loadMembers()
    }
    private fun loadMembers() {
        viewModelScope.launch {
            memberUseCases.getAllMembers().collect { members ->
                _state.value = _state.value.copy(
                    members = members,
                    // اگر عضوی موجود است، اولین عضو را به صورت پیش‌فرض انتخاب کن
                    selectedMemberId = if (_state.value.selectedMemberId == -1L && members.isNotEmpty())
                        members.first().id else _state.value.selectedMemberId
                )
            }
        }
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
                val clean = event.value.replace(",", "").filter { it.isDigit() }

                _state.value = _state.value.copy(
                    amountRaw = clean,
                    amount = formatAmount(clean)
                )
            }

            is AddEditTransactionEvent.TypeChanged -> {
                Log.d(
                    "TYPE",
                    event.type.name
                )
                _state.value = _state.value.copy(type = event.type)
            }

            is AddEditTransactionEvent.CategorySelected -> {
                _state.value = _state.value.copy(selectedCategoryName = event.categoryName)
            }

            is AddEditTransactionEvent.WalletSelected -> {
                _state.value = _state.value.copy(selectedWalletId = event.walletId)
            }

            is AddEditTransactionEvent.MemberSelected -> {
                _state.value = _state.value.copy(selectedMemberId = event.memberId)
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
            val amount = _state.value.amountRaw.toDoubleOrNull() ?: 0.0

            // چک کردن برای مقدار پیش‌فرض -1
            if (amount < 0.0) {
                _eventFlow.emit(UiEvent.ShowSnackbar("مبلغ وارد شده صحیح نیست"))
                return@launch
            }

            // چک کردن برای null
            if (_state.value.selectedWalletId == null || _state.value.selectedWalletId == -1L) {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب بانکی انتخاب شده معتبر نیست."))
                return@launch
            }

            // - چک کردن شناسه کیف پول
            val walletId = _state.value.selectedWalletId?: run {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب انتخاب نشده است"))
                return@launch
            }

            // - چک کردن خود کیف پول
            val wallet = walletUseCases.getWalletById(walletId)
            if (wallet == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب پیدا نشد"))
                return@launch
            }

            // - چک کردن همخوانی مبلغ کیف پول انتخابی و میزان هزینه
            if (_state.value.type == TransactionType.EXPENSE) {
                if (wallet.balance < amount) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("موجودی کیف پول کافی نیست"))
                    return@launch
                }
            }

            try {
                Log.d(
                    "SAVE",
                    _state.value.type.name
                )
                transactionUseCases.insertTransaction(
                    Transaction(
                        amount = amount,
                        type = _state.value.type,
                        categoryName = _state.value.selectedCategoryName ?: "عمومی",
                        walletId = _state.value.selectedWalletId!!, // <--- اضافه کردن علامت !! برای رفع خطا
                        memberId =_state.value.selectedMemberId!!,
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

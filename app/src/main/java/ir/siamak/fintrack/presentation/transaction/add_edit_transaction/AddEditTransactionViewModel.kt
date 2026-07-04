package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.extensions.formatAmount
import ir.siamak.fintrack.core.extensions.persianToEnglishDigits
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.repository.TagRepository // اضافه شد
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases,
    private val walletUseCases: WalletUseCases,
    private val memberUseCases: MemberUseCases,
    private val tagRepository: TagRepository // تزریق ریپازیتوری تگ‌ها
) : ViewModel() {

    private val _state = mutableStateOf(AddEditTransactionState())
    val state: State<AddEditTransactionState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadWallets()
        loadMembers()
        loadTags() // اضافه شدن متد بارگذاری تگ‌ها
    }

    private fun loadTags() {
        viewModelScope.launch {
            tagRepository.getAllTags().collect { tagsList ->
                _state.value = _state.value.copy(
                    tags = tagsList
                )
            }
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            memberUseCases.getAllMembers().collect { members ->
                _state.value = _state.value.copy(
                    members = members,
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
                    selectedWalletId = if (_state.value.selectedWalletId == -1L && wallets.isNotEmpty())
                        wallets.first().id else _state.value.selectedWalletId
                )
            }
        }
    }

    fun onEvent(event: AddEditTransactionEvent) {
        when (event) {
            is AddEditTransactionEvent.ToWalletSelected -> {
                _state.value = _state.value.copy(selectedToWalletId = event.walletId)
            }

            is AddEditTransactionEvent.EnteredAmount -> {
                val clean = event.value.persianToEnglishDigits().replace(",", "").filter { it.isDigit() }
                _state.value = _state.value.copy(
                    amountRaw = clean,
                    amount = formatAmount(clean)
                )
            }

            is AddEditTransactionEvent.TypeChanged -> {
                Log.d("TYPE", event.type.name)
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

            is AddEditTransactionEvent.TagToggled -> {
                val currentTags = _state.value.selectedTagIds
                val newTags = if (event.tagId in currentTags) {
                    currentTags - event.tagId
                } else {
                    currentTags + event.tagId
                }
                _state.value = _state.value.copy(selectedTagIds = newTags)
            }
        }
    }

    private fun saveTransaction() {
        viewModelScope.launch {
            val amount = _state.value.amountRaw.toDoubleOrNull() ?: 0.0

            if (amount <= 0.0) {
                _eventFlow.emit(UiEvent.ShowSnackbar("مبلغ وارد شده باید بیشتر از صفر باشد"))
                return@launch
            }

            val walletId = _state.value.selectedWalletId ?: run {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب مبدا انتخاب نشده است"))
                return@launch
            }

            val wallet = walletUseCases.getWalletById(walletId) ?: run {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب مبدا پیدا نشد"))
                return@launch
            }

            if (_state.value.type == TransactionType.EXPENSE || _state.value.type == TransactionType.TRANSFER) {
                if (wallet.balance < amount) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("موجودی حساب مبدا کافی نیست"))
                    return@launch
                }
            }

            if (_state.value.type == TransactionType.TRANSFER) {
                val toWalletId = _state.value.selectedToWalletId
                if (toWalletId == null || toWalletId == -1L) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("حساب مقصد انتخاب نشده است"))
                    return@launch
                }
                if (walletId == toWalletId) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("حساب مبدا و مقصد نمی‌توانند یکسان باشند"))
                    return@launch
                }
            }

            try {
                // توجه: اگر کلاس Transaction شما از فیلد tagIds پشتیبانی می‌کند،
                // مقدار `tagIds = _state.value.selectedTagIds` را در اینجا اضافه کنید.
                transactionUseCases.insertTransaction(
                    Transaction(
                        amount = amount,
                        type = _state.value.type,
                        categoryName = if (_state.value.type == TransactionType.TRANSFER) "انتقال" else (_state.value.selectedCategoryName ?: "عمومی"),
                        walletId = walletId,
                        toWalletId = if (_state.value.type == TransactionType.TRANSFER) _state.value.selectedToWalletId else null,
                        memberId = _state.value.selectedMemberId ?: -1L,
                        date = System.currentTimeMillis(),
                        note = _state.value.note
                        // tagIds = _state.value.selectedTagIds (اگر در مدل دیتابیس تراکنش تعریف شده است)
                    )
                )
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("خطا در ذخیره: ${e.message}"))
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }
}

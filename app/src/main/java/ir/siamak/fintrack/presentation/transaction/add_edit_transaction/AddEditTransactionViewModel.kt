package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.extensions.formatAmount
import ir.siamak.fintrack.core.extensions.persianToEnglishDigits
import ir.siamak.fintrack.core.extensions.toPersianDigits
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.repository.TagRepository
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * ویومدل صفحه ثبت و ویرایش تراکنش.
 */
@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases,
    private val walletUseCases: WalletUseCases,
    private val memberUseCases: MemberUseCases,
    private val tagRepository: TagRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(AddEditTransactionState())
    val state: State<AddEditTransactionState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadWallets()
        loadMembers()
        loadTags()

        val transactionId = savedStateHandle.get<Long>("transactionId")
        if (transactionId != null && transactionId != -1L) {
            _state.value = _state.value.copy(currentTransactionId = transactionId)
            viewModelScope.launch {
                loadTransaction(transactionId)
            }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            tagRepository.getAllTags().collect { tagsList ->
                val currentType = _state.value.type
                val filteredTags = tagsList.filterAllowedFor(currentType)

                val allowedIds = filteredTags.map { it.id }.toSet()
                val validSelectedTagIds = _state.value.selectedTagIds.filter { it in allowedIds }

                _state.value = _state.value.copy(
                    tags = tagsList,
                    filteredTags = filteredTags,
                    selectedTagIds = validSelectedTagIds,
                    selectedCategoryName = resolveCategoryForType(
                        type = currentType,
                        tagIds = validSelectedTagIds
                    )
                )
            }
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            memberUseCases.getAllMembers().collect { members ->
                _state.value = _state.value.copy(
                    members = members,
                    selectedMemberId = when {
                        _state.value.currentTransactionId != null -> _state.value.selectedMemberId
                        _state.value.selectedMemberId == null && members.isNotEmpty() -> members.first().id
                        else -> _state.value.selectedMemberId
                    }
                )
            }
        }
    }

    private fun loadWallets() {
        viewModelScope.launch {
            walletUseCases.getAllWallets().collect { wallets ->
                _state.value = _state.value.copy(
                    wallets = wallets,
                    selectedWalletId = when {
                        _state.value.currentTransactionId != null -> _state.value.selectedWalletId
                        _state.value.selectedWalletId == null && wallets.isNotEmpty() -> wallets.first().id
                        else -> _state.value.selectedWalletId
                    }
                )
            }
        }
    }

    private suspend fun loadTransaction(transactionId: Long) {
        val transaction = transactionUseCases.getTransactionById(transactionId) ?: return

        val rawAmount = BigDecimal.valueOf(transaction.amount)
            .stripTrailingZeros()
            .toPlainString()

        _state.value = _state.value.copy(
            currentTransactionId = transaction.id,
            amountRaw = rawAmount,
            amount = formatAmount(rawAmount).toPersianDigits(),
            type = transaction.type,
            selectedWalletId = transaction.walletId,
            selectedToWalletId = transaction.toWalletId,
            selectedMemberId = transaction.memberId,
            selectedCategoryName = transaction.categoryName,
            note = transaction.note,
            selectedTagIds = transaction.tags.map { it.id }
        )

        // فیلتر کردن تگ‌ها بعد از بارگذاری اطلاعات تراکنش جهت همگام‌سازی تگ‌های انتخابی
        filterTagsForType(transaction.type, clearInvalidSelected = false)
    }

    fun onEvent(event: AddEditTransactionEvent) {
        when (event) {
            is AddEditTransactionEvent.EnteredAmount -> handleAmountChange(event.value)
            is AddEditTransactionEvent.TypeChanged -> handleTypeChange(event.type)
            is AddEditTransactionEvent.CategorySelected -> {
                _state.value = _state.value.copy(selectedCategoryName = event.categoryName)
            }
            is AddEditTransactionEvent.WalletSelected -> {
                _state.value = _state.value.copy(selectedWalletId = event.walletId)
            }
            is AddEditTransactionEvent.ToWalletSelected -> {
                _state.value = _state.value.copy(selectedToWalletId = event.walletId)
            }
            is AddEditTransactionEvent.MemberSelected -> {
                _state.value = _state.value.copy(selectedMemberId = event.memberId)
            }
            is AddEditTransactionEvent.EnteredNote -> {
                _state.value = _state.value.copy(note = event.value)
            }
            is AddEditTransactionEvent.TagToggled -> handleTagToggle(event.tagId)
            AddEditTransactionEvent.SaveTransaction -> saveTransaction()
            AddEditTransactionEvent.DeleteTransaction -> deleteTransaction()
        }
    }

    private fun handleAmountChange(value: String) {
        val clean = value
            .persianToEnglishDigits()
            .replace(",", "")
            .filter { it.isDigit() }

        _state.value = _state.value.copy(
            amountRaw = clean,
            amount = formatAmount(clean).toPersianDigits()
        )
    }

    private fun handleTypeChange(type: TransactionType) {
        val filteredTags = _state.value.tags.filterAllowedFor(type)
        val allowedIds = filteredTags.map { it.id }.toSet()
        val validSelectedTagIds = _state.value.selectedTagIds.filter { it in allowedIds }

        _state.value = _state.value.copy(
            type = type,
            filteredTags = filteredTags,
            selectedTagIds = validSelectedTagIds,
            selectedToWalletId = if (type == TransactionType.TRANSFER) {
                _state.value.selectedToWalletId
            } else {
                null
            },
            selectedCategoryName = resolveCategoryForType(
                type = type,
                tagIds = validSelectedTagIds
            )
        )
    }

    private fun filterTagsForType(type: TransactionType, clearInvalidSelected: Boolean) {
        val allTags = _state.value.tags
        val filtered = allTags.filter { it.allowedType == null || it.allowedType == type }
        val updatedTagIds = if (clearInvalidSelected) {
            val allowedIds = filtered.map { it.id }.toSet()
            _state.value.selectedTagIds.filter { it in allowedIds }
        } else {
            _state.value.selectedTagIds
        }

        val nextCategory = if (type == TransactionType.TRANSFER) {
            "انتقال"
        } else {
            resolveCategoryFromTagIds(updatedTagIds)
        }

        _state.value = _state.value.copy(
            filteredTags = filtered,
            selectedTagIds = updatedTagIds,
            selectedCategoryName = nextCategory
        )
    }

    private fun handleTagToggle(tagId: Long) {
        val isAllowed = _state.value.filteredTags.any { it.id == tagId }
        if (!isAllowed) return

        val updatedTagIds = if (tagId in _state.value.selectedTagIds) {
            _state.value.selectedTagIds - tagId
        } else {
            _state.value.selectedTagIds + tagId
        }

        _state.value = _state.value.copy(
            selectedTagIds = updatedTagIds,
            selectedCategoryName = resolveCategoryForType(
                type = _state.value.type,
                tagIds = updatedTagIds
            )
        )
    }

    private fun saveTransaction() {
        viewModelScope.launch {
            val amount = _state.value.amountRaw.toDoubleOrNull() ?: 0.0
            if (amount <= 0.0) {
                _eventFlow.emit(UiEvent.ShowSnackbar("مبلغ وارد شده باید بیشتر از صفر باشد"))
                return@launch
            }

            val walletId = _state.value.selectedWalletId
            if (walletId == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب مبدا انتخاب نشده است"))
                return@launch
            }

            val wallet = walletUseCases.getWalletById(walletId)
            if (wallet == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("حساب مبدا پیدا نشد"))
                return@launch
            }

            if (_state.value.type == TransactionType.TRANSFER) {
                val toWalletId = _state.value.selectedToWalletId
                if (toWalletId == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("حساب مقصد انتخاب نشده است"))
                    return@launch
                }
                if (toWalletId == walletId) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("حساب مبدا و مقصد نمی‌توانند یکسان باشند"))
                    return@launch
                }
            }

            val selectedTags = _state.value.tags.filter { tag ->
                tag.id in _state.value.selectedTagIds
            }

            val resolvedCategoryName = when (_state.value.type) {
                TransactionType.TRANSFER -> "انتقال"
                else -> resolveCategoryFromSelectedTags()
            }

            val existingTransaction = _state.value.currentTransactionId?.let {
                transactionUseCases.getTransactionById(it)
            }

            val transaction = Transaction(
                id = _state.value.currentTransactionId ?: 0L,
                amount = amount,
                type = _state.value.type,
                categoryName = resolvedCategoryName,
                walletId = walletId,
                toWalletId = if (_state.value.type == TransactionType.TRANSFER) {
                    _state.value.selectedToWalletId
                } else {
                    null
                },
                memberId = _state.value.selectedMemberId ?: -1L,
                date = existingTransaction?.date ?: System.currentTimeMillis(),
                note = _state.value.note,
                tags = selectedTags
            )

            try {
                if (_state.value.currentTransactionId == null) {
                    transactionUseCases.insertTransaction(transaction)
                } else {
                    transactionUseCases.updateTransaction(transaction)
                }
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(
                    UiEvent.ShowSnackbar("خطا در ذخیره تراکنش: ${e.message.orEmpty()}")
                )
            }
        }
    }

    private fun deleteTransaction() {
        viewModelScope.launch {
            val currentId = _state.value.currentTransactionId
            if (currentId == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("تراکنشی برای حذف وجود ندارد"))
                return@launch
            }

            val transaction = transactionUseCases.getTransactionById(currentId)
            if (transaction == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("تراکنش پیدا نشد"))
                return@launch
            }

            try {
                transactionUseCases.deleteTransaction(transaction)
                _eventFlow.emit(UiEvent.DeleteSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("خطا در حذف تراکنش: ${e.message.orEmpty()}"))
            }
        }
    }

    private fun resolveCategoryFromSelectedTags(): String {
        return resolveCategoryFromTagIds(_state.value.selectedTagIds)
    }

    private fun resolveCategoryFromTagIds(tagIds: List<Long>): String {
        return _state.value.tags
            .firstOrNull { it.id in tagIds }
            ?.name
            ?.takeIf { it.isNotBlank() }
            ?: "سایر"
    }

    private fun List<Tag>.filterAllowedFor(type: TransactionType): List<Tag> {
        return filter { tag ->
            tag.allowedType == null || tag.allowedType == type
        }
    }

    private fun resolveCategoryForType(
        type: TransactionType,
        tagIds: List<Long>
    ): String {
        if (type == TransactionType.TRANSFER) return "انتقال"

        return _state.value.tags
            .firstOrNull { tag -> tag.id in tagIds }
            ?.name
            ?.takeIf { it.isNotBlank() }
            ?: "سایر"
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
        object DeleteSuccess : UiEvent()
    }
}

package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.extensions.formatAmount
import ir.siamak.fintrack.core.extensions.persianToEnglishDigits
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
 *
 * مسئولیت‌ها:
 * - بارگذاری داده‌های اولیه مانند حساب‌ها، اعضا و تگ‌ها
 * - بارگذاری تراکنش در حالت ویرایش
 * - نگهداری state فرم
 * - اعتبارسنجی و ذخیره/ویرایش/حذف تراکنش
 * - همگام‌سازی دسته‌بندی نمایشی با تگ انتخاب‌شده در UI فعلی
 *
 * نکته:
 * در UI فعلی، کاربر دسته‌بندی جداگانه انتخاب نمی‌کند و فقط تگ‌ها را می‌بیند.
 * بنابراین برای جلوگیری از ذخیره شدن مقدار پیش‌فرض "سایر"، دسته‌بندی را
 * از اولین تگ انتخاب‌شده استخراج می‌کنیم؛ مگر در حالت انتقال که دسته‌بندی
 * همیشه "انتقال" است.
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



    /**
     * واکشی تگ‌ها و قرار دادن آن‌ها در state.
     */
    private fun loadTags() {
        viewModelScope.launch {
            tagRepository.getAllTags().collect { tagsList ->
                _state.value = _state.value.copy(tags = tagsList)
            }
        }
    }

    /**
     * واکشی اعضا و تعیین مقدار پیش‌فرض در حالت ثبت.
     */
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

    /**
     * واکشی حساب‌ها و تعیین مقدار پیش‌فرض در حالت ثبت.
     */
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

    /**
     * بارگذاری تراکنش موجود برای ویرایش.
     *
     * این متد علاوه بر پر کردن فرم، تگ‌های انتخاب‌شده و مبلغ خام/نمایشی را
     * نیز بازیابی می‌کند.
     */
    private suspend fun loadTransaction(transactionId: Long) {
        val transaction = transactionUseCases.getTransactionById(transactionId) ?: return

        handleTypeChange(
            type = transaction.type,
            clearSelectedTags = false
        )

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
    }




    /**
     * پردازش رویدادهای UI.
     */
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

    /**
     * مدیریت تغییر مبلغ ورودی.
     *
     * ورودی کاربر:
     * - از ارقام فارسی/انگلیسی پشتیبانی می‌کند
     * - ویرگول‌ها را حذف می‌کند
     * - فقط رقم نگه می‌دارد
     *
     * سپس مقدار خام و مقدار فرمت‌شده نمایشی به‌روزرسانی می‌شوند.
     */
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

    /**
     * مدیریت تغییر نوع تراکنش.
     *
     * در حالت انتقال:
     * - دسته‌بندی همیشه "انتقال" است
     * - حساب مقصد مجاز است
     *
     * در حالت غیرانتقال:
     * - حساب مقصد پاک می‌شود
     * - اگر قبلاً "انتقال" بوده، دسته‌بندی از روی اولین تگ انتخابی یا "سایر" بازسازی می‌شود
     */
    private fun handleTypeChange(
        type: TransactionType,
        clearSelectedTags: Boolean = true
    ) {
        val nextCategory = if (type == TransactionType.TRANSFER) {
            "انتقال"
        } else if (_state.value.type == TransactionType.TRANSFER) {
            resolveCategoryFromTagIds(_state.value.selectedTagIds)
        } else {
            _state.value.selectedCategoryName
        }

        _state.value = _state.value.copy(
            type = type,
            selectedCategoryName = nextCategory,
            selectedToWalletId = if (type == TransactionType.TRANSFER) {
                _state.value.selectedToWalletId
            } else {
                null
            },
            selectedTagIds = if (clearSelectedTags) emptyList() else _state.value.selectedTagIds
        )
    }




    /**
     * انتخاب یا لغو انتخاب تگ.
     *
     * چون UI فعلی دسته‌بندی جداگانه ندارد، پس از هر تغییر تگ:
     * - اگر نوع تراکنش انتقال نباشد
     * - دسته‌بندی از اولین تگ انتخاب‌شده استخراج می‌شود
     * - و اگر هیچ تگی انتخاب نباشد، "سایر" قرار می‌گیرد
     */
    private fun handleTagToggle(tagId: Long) {
        val currentTags = _state.value.selectedTagIds
        val updatedTags = if (tagId in currentTags) {
            currentTags - tagId
        } else {
            currentTags + tagId
        }

        val nextCategory = if (_state.value.type  == TransactionType.TRANSFER) {
            "انتقال"
        } else {
            resolveCategoryFromTagIds(updatedTags)
        }

        _state.value = _state.value.copy(
            selectedTagIds = updatedTags,
            selectedCategoryName = nextCategory
        )
    }

    /**
     * ذخیره یا ویرایش تراکنش با اعتبارسنجی کامل.
     */
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

    /**
     * حذف تراکنش جاری.
     */
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

    /**
     * استخراج دسته‌بندی از تگ‌های انتخاب‌شده فعلی.
     */
    private fun resolveCategoryFromSelectedTags(): String {
        return resolveCategoryFromTagIds(_state.value.selectedTagIds)
    }

    /**
     * استخراج دسته‌بندی از روی اولین تگ انتخاب‌شده.
     *
     * اگر هیچ تگی انتخاب نشده باشد، مقدار پیش‌فرض "سایر" برمی‌گردد.
     */
    private fun resolveCategoryFromTagIds(tagIds: List<Long>): String {
        return _state.value.tags
            .firstOrNull { it.id in tagIds }
            ?.name
            ?.takeIf { it.isNotBlank() }
            ?: "سایر"
    }

    /**
     * تبدیل ارقام انگلیسی رشته به فارسی برای نمایش در UI.
     */
    private fun String.toPersianDigits(): String {
        return buildString(length) {
            for (char in this@toPersianDigits) {
                append(
                    when (char) {
                        '0' -> '۰'
                        '1' -> '۱'
                        '2' -> '۲'
                        '3' -> '۳'
                        '4' -> '۴'
                        '5' -> '۵'
                        '6' -> '۶'
                        '7' -> '۷'
                        '8' -> '۸'
                        '9' -> '۹'
                        else -> char
                    }
                )
            }
        }
    }

    /**
     * رویدادهای یک‌بارمصرف UI.
     */
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
        object DeleteSuccess : UiEvent()
    }
}

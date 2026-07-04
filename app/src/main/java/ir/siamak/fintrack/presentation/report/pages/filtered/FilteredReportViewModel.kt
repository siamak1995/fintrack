package ir.siamak.fintrack.presentation.report.pages.filtered

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.report.model.FilteredTransactionResult
import ir.siamak.fintrack.domain.report.usecases.GetFilteredTransactionsUseCase
import ir.siamak.fintrack.domain.repository.MemberRepository
import ir.siamak.fintrack.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class FilteredReportViewModel @Inject constructor(
    private val getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase,
    private val walletRepository: WalletRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilteredReportUiState())
    val uiState: StateFlow<FilteredReportUiState> = _uiState.asStateFlow()

    init {
        loadDropdownData()
        applyFilters()
    }

    private fun loadDropdownData() {
        viewModelScope.launch {
            try {
                val walletsList = walletRepository.getAllWallets().first()
                val membersList = memberRepository.getAllMembers().first()
                _uiState.update {
                    it.copy(wallets = walletsList, members = membersList)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "خطا در بارگذاری اطلاعات پایه") }
            }
        }
    }

    fun onMemberSelected(memberId: Long?) {
        _uiState.update { it.copy(selectedMemberId = memberId) }
        applyFilters()
    }

    fun onWalletSelected(walletId: Long?) {
        _uiState.update { it.copy(selectedWalletId = walletId) }
        applyFilters()
    }

    fun onTypeSelected(type: TransactionType?) {
        _uiState.update { it.copy(selectedType = type) }
        applyFilters()
    }

    fun showDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = true) }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    fun clearDateRange() {
        _uiState.update { it.copy(selectedFromDate = null, selectedToDate = null, isDatePickerVisible = false) }
        applyFilters()
    }

    fun onDateRangeSelected(fromDate: PersianDate?, toDate: PersianDate?) {
        val (normalizedFrom, normalizedTo) = normalizeDateRange(fromDate, toDate)
        _uiState.update {
            it.copy(
                selectedFromDate = normalizedFrom,
                selectedToDate = normalizedTo,
                isDatePickerVisible = false
            )
        }
        applyFilters()
    }

    fun resetAllFilters() {
        _uiState.update {
            it.copy(
                selectedFromDate = null,
                selectedToDate = null,
                selectedMemberId = null,
                selectedWalletId = null,
                selectedType = null
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val state = _uiState.value
                val start = state.selectedFromDate?.toStartOfDayTimestamp()
                val end = state.selectedToDate?.toEndOfDayTimestamp()

                val results = getFilteredTransactionsUseCase(
                    startTimestamp = start,
                    endTimestamp = end,
                    memberId = state.selectedMemberId,
                    walletId = state.selectedWalletId,
                    transactionType = state.selectedType
                )

                val uiModels = results.map { it.toUiModel() }

                _uiState.update {
                    it.copy(transactions = uiModels, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun normalizeDateRange(fromDate: PersianDate?, toDate: PersianDate?): Pair<PersianDate?, PersianDate?> {
        if (fromDate == null || toDate == null) return fromDate to toDate
        return if (fromDate.isBeforeOrEqual(toDate)) fromDate to toDate else toDate to fromDate
    }

    private fun PersianDate.toStartOfDayTimestamp(): Long {
        val gregorianDate = JalaliDateConverter.toGregorian(this)
        return gregorianDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun PersianDate.toEndOfDayTimestamp(): Long {
        val gregorianDate = JalaliDateConverter.toGregorian(this)
        return gregorianDate.atTime(LocalTime.of(23, 59, 59, 999_000_000))
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun PersianDate.isBeforeOrEqual(other: PersianDate): Boolean {
        return when {
            year != other.year -> year < other.year
            month != other.month -> month < other.month
            else -> day <= other.day
        }
    }

    // تبدیل مدل لایه دامنه به مدل بهینه نمایش (UI Model)
    private fun FilteredTransactionResult.toUiModel(): FilteredReportUiModel {
        val tx = transaction

        val typeLabel = when (tx.type) {
            TransactionType.INCOME -> "واریز"
            TransactionType.EXPENSE -> "برداشت"
            TransactionType.TRANSFER -> "انتقال"
        }

        val typeColor = when (tx.type) {
            TransactionType.INCOME -> Color(0xFF2E7D32)
            TransactionType.EXPENSE -> Color(0xFFC62828)
            TransactionType.TRANSFER -> Color(0xFF1565C0)
        }

        val signedAmount = when (tx.type) {
            TransactionType.INCOME -> tx.amount.toLong()
            TransactionType.EXPENSE -> -tx.amount.toLong()
            TransactionType.TRANSFER -> 0L
        }

        return FilteredReportUiModel(
            id = tx.id,
            title = tx.categoryName,
            note = tx.note,
            amountText = tx.amount.toLong().toAmountText(),
            amountColor = typeColor,
            type = tx.type,
            typeLabel = typeLabel,
            typeColor = typeColor,
            walletName = walletName,
            walletColor = parseColorOrDefault(walletColor),
            memberName = memberName,
            memberColor = parseColorOrDefault(memberColor),
            dateText = tx.date.toPersianDateString(),
            signedAmount = signedAmount
        )
    }

    private fun parseColorOrDefault(colorHex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFF7F8C8D)
        }
    }

    private fun Long.toPersianDateString(): String {
        val localDate = java.time.Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
        val persianDate = JalaliDateConverter.fromGregorian(localDate)
        return "${persianDate.year}/${persianDate.month}/${persianDate.day}"
    }

    private fun Long.toAmountText(): String = "%,d تومان".format(this)
}

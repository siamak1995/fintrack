package ir.siamak.fintrack.presentation.report.pages.transactionsHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.repository.TagRepository
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HistoryReportViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases,
    private val walletUseCases: WalletUseCases,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryReportUiState(isLoading = true))
    val uiState: StateFlow<HistoryReportUiState> = _uiState.asStateFlow()

    private var allTransactions: List<Transaction> = emptyList()
    private var allWallets: Map<Long, String> = emptyMap()

    init {
        loadData()
    }

    fun showDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = true) }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    fun clearDateRange() {
        _uiState.update {
            it.copy(
                selectedFromDate = null,
                selectedToDate = null,
                isDatePickerVisible = false
            )
        }
        refreshReport()
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
        refreshReport()
    }

    fun onTypeSelected(type: HistoryTransactionTypeFilter) {
        _uiState.update { it.copy(selectedType = type) }
        refreshReport()
    }

    fun toggleTag(tagId: Long) {
        _uiState.update { current ->
            val updated = current.selectedTagIds.toMutableSet()
            if (tagId in updated) {
                updated.remove(tagId)
            } else {
                updated.add(tagId)
            }
            current.copy(selectedTagIds = updated)
        }
        refreshReport()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                combine(
                    transactionUseCases.getAllTransactions(),
                    walletUseCases.getAllWallets(),
                    tagRepository.getAllTags()
                ) { transactions, wallets, tags ->
                    Triple(transactions, wallets, tags)
                }.collect { (transactions, wallets, tags) ->

                    allTransactions = transactions
                    allWallets = wallets.associate { wallet -> wallet.id to wallet.name }

                    _uiState.update {
                        it.copy(
                            availableTags = tags.map { tag ->
                                HistoryTagUiModel(
                                    id = tag.id,
                                    name = tag.name,
                                    color = tag.color
                                )
                            }
                        )
                    }

                    refreshReport()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "خطا در بارگذاری گزارش تاریخچه"
                    )
                }
            }
        }
    }

    private fun refreshReport() {
        val state = _uiState.value
        val startTimestamp = state.selectedFromDate?.toStartOfDayTimestamp()
        val endTimestamp = state.selectedToDate?.toEndOfDayTimestamp()

        val filtered = allTransactions.filter { transaction ->
            val matchesDate = when {
                startTimestamp != null && endTimestamp != null ->
                    transaction.date in startTimestamp..endTimestamp
                startTimestamp != null ->
                    transaction.date >= startTimestamp
                endTimestamp != null ->
                    transaction.date <= endTimestamp
                else -> true
            }

            val matchesType = when (state.selectedType) {
                HistoryTransactionTypeFilter.ALL -> true
                HistoryTransactionTypeFilter.INCOME -> transaction.type == TransactionType.INCOME
                HistoryTransactionTypeFilter.EXPENSE -> transaction.type == TransactionType.EXPENSE
                HistoryTransactionTypeFilter.TRANSFER -> transaction.type == TransactionType.TRANSFER
            }

            val matchesTag = if (state.selectedTagIds.isEmpty()) {
                true
            } else {
                transaction.tags.any { it.id in state.selectedTagIds }
            }

            matchesDate && matchesType && matchesTag
        }

        val totalIncome = filtered
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.toLong() }

        val totalExpense = filtered
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.toLong() }

        val totalTransfer = filtered
            .filter { it.type == TransactionType.TRANSFER }
            .sumOf { it.amount.toLong() }

        val chartItems = filtered
            .groupBy { allWallets[it.walletId] ?: "بدون حساب" }
            .map { (walletName, transactions) ->
                HistoryChartItemUiModel(
                    label = walletName,
                    value = transactions.sumOf { it.amount }.toFloat()
                )
            }
            .sortedByDescending { it.value }

        val transactionItems = filtered
            .sortedByDescending { it.date }
            .map { transaction ->
                HistoryTransactionUiModel(
                    id = transaction.id,
                    title = transaction.categoryName ?: transaction.type.name,
                    note = transaction.note,
                    amount = transaction.amount.toLong(),
                    type = transaction.type.toHistoryFilterType(),
                    dateText = transaction.date.toString(),
                    walletName = allWallets[transaction.walletId] ?: "بدون حساب",
                    categoryName = transaction.categoryName,
                    tags = transaction.tags.map { tag ->
                        HistoryTagUiModel(
                            id = tag.id,
                            name = tag.name,
                            color = tag.color
                        )
                    }
                )
            }

        _uiState.update {
            it.copy(
                summary = HistorySummaryUiState(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    totalTransfer = totalTransfer,
                    netBalance = totalIncome - totalExpense
                ),
                chartItems = chartItems,
                transactions = transactionItems,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private fun normalizeDateRange(
        fromDate: PersianDate?,
        toDate: PersianDate?
    ): Pair<PersianDate?, PersianDate?> {
        if (fromDate == null || toDate == null) return fromDate to toDate
        return if (fromDate.isBeforeOrEqual(toDate)) {
            fromDate to toDate
        } else {
            toDate to fromDate
        }
    }

    private fun PersianDate.toStartOfDayTimestamp(): Long {
        val gregorianDate = toGregorianLocalDate()
        return gregorianDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun PersianDate.toEndOfDayTimestamp(): Long {
        val gregorianDate = toGregorianLocalDate()
        return gregorianDate
            .atTime(LocalTime.of(23, 59, 59, 999_000_000))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun PersianDate.toGregorianLocalDate(): LocalDate {
        JalaliCalendarEngine.requireValidDate(this)
        return JalaliDateConverter.toGregorian(this)
    }

    private fun PersianDate.isBeforeOrEqual(other: PersianDate): Boolean {
        return when {
            year != other.year -> year < other.year
            month != other.month -> month < other.month
            else -> day <= other.day
        }
    }

    private fun TransactionType.toHistoryFilterType(): HistoryTransactionTypeFilter {
        return when (this) {
            TransactionType.INCOME -> HistoryTransactionTypeFilter.INCOME
            TransactionType.EXPENSE -> HistoryTransactionTypeFilter.EXPENSE
            TransactionType.TRANSFER -> HistoryTransactionTypeFilter.TRANSFER
        }
    }
}

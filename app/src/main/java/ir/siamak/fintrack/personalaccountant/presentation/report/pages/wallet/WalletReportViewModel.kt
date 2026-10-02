package ir.siamak.fintrack.personalaccountant.presentation.report.pages.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.personalaccountant.domain.report.usecases.GetWalletReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WalletReportViewModel @Inject constructor(
    private val getWalletReportUseCase: GetWalletReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletReportUiState())
    val uiState: StateFlow<WalletReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun showDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = true) }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    fun clearDateRange() {
        _uiState.update { it.copy(selectedFromDate = null, selectedToDate = null, isDatePickerVisible = false) }
        loadReport()
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
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _uiState.value
                val start = state.selectedFromDate?.toStartOfDayTimestamp()
                val end = state.selectedToDate?.toEndOfDayTimestamp()

                val reports = getWalletReportUseCase(start, end)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = reports.map { report -> report.toUiModel() }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "خطا در بارگذاری گزارش کیف پول‌ها"
                    )
                }
            }
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
        val gregorianDate = this.toGregorianLocalDate()
        return gregorianDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun PersianDate.toEndOfDayTimestamp(): Long {
        val gregorianDate = this.toGregorianLocalDate()
        return gregorianDate
            .atTime(LocalTime.of(23, 59, 59, 999_000_000))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun PersianDate.toGregorianLocalDate(): LocalDate {
        JalaliCalendarEngine.requireValidDate(this)
        // استفاده از مبدل پروژه شما:
        return JalaliDateConverter.toGregorian(this)
    }

    private fun PersianDate.isBeforeOrEqual(other: PersianDate): Boolean {
        return when {
            year != other.year -> year < other.year
            month != other.month -> month < other.month
            else -> day <= other.day
        }
    }
}


package ir.siamak.fintrack.personalaccountant.presentation.report.pages.visual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.domain.report.usecases.*
import ir.siamak.fintrack.personalaccountant.domain.usecase.transaction.GetAllTransactionsUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

data class VisualReportUiState(val loading: Boolean = true, val report: VisualReport? = null, val error: String? = null)
@HiltViewModel
class VisualReportViewModel @Inject constructor(transactions: GetAllTransactionsUseCase, calculator: CalculateVisualReportUseCase) : ViewModel() {
    private val range = MutableStateFlow(LocalDate.now().minusDays(29) to LocalDate.now())
    val state = combine(transactions(),range) { rows,dates -> VisualReportUiState(false,calculator(rows,dates.first,dates.second)) }
        .catch { emit(VisualReportUiState(false,error = "خواندن گزارش ناموفق بود: " + it.message)) }
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),VisualReportUiState())
    fun range(from: LocalDate, to: LocalDate) { range.value = if(from <= to) from to to else to to from }
}

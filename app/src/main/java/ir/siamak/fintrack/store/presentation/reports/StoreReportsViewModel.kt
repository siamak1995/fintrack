package ir.siamak.fintrack.store.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.repository.InvoiceRepository
import ir.siamak.fintrack.store.domain.usecase.store.CalculateStoreReportUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

data class StoreReportsUiState(val loading: Boolean = true, val invoices: List<Invoice> = emptyList(), val report: StoreReport? = null, val error: String? = null)

@HiltViewModel
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class StoreReportsViewModel @Inject constructor(repository: InvoiceRepository, calculator: CalculateStoreReportUseCase) : ViewModel() {
    private val filter = MutableStateFlow(StoreReportFilter())
    private val reload = MutableStateFlow(0L)
    val state = reload.flatMapLatest { combine(repository.observeAll(), filter) { invoices, query ->
        try { StoreReportsUiState(false, invoices, calculator(invoices, query)) }
        catch(cancelled: CancellationException) { throw cancelled }
        catch(error: Exception) { StoreReportsUiState(false, invoices, error = error.message ?: "گزارش قابل محاسبه نیست") }
    }.catch { emit(StoreReportsUiState(false, error = "خواندن گزارش ناموفق بود: " + it.message)) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StoreReportsUiState())

    fun change(transform: (StoreReportFilter) -> StoreReportFilter) { filter.update { old ->
        var next = transform(old).copy(page = 0)
        if(next.from > next.to) next = if(next.from != old.from) next.copy(to = next.from) else next.copy(from = next.to)
        val comparisonFrom = next.comparisonFrom; val comparisonTo = next.comparisonTo
        if(comparisonFrom != null && comparisonTo != null && comparisonFrom > comparisonTo) next =
            if(comparisonFrom != old.comparisonFrom) next.copy(comparisonTo = comparisonFrom) else next.copy(comparisonFrom = comparisonTo)
        next
    } }
    fun page(number: Int) { filter.update { it.copy(page = number.coerceAtLeast(0)) } }
    fun reset() { filter.value = StoreReportFilter() }
    fun retry() { reload.update { it + 1 } }
}

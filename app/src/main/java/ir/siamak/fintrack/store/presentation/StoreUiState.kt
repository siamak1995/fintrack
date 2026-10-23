package ir.siamak.fintrack.store.presentation
import ir.siamak.fintrack.store.domain.model.StoreWorkspace
import ir.siamak.fintrack.store.domain.model.StoreOverview
data class StoreUiState(val loading: Boolean = true, val data: StoreWorkspace = StoreWorkspace(), val overview: StoreOverview = StoreOverview(), val busy: Boolean = false, val error: String? = null)

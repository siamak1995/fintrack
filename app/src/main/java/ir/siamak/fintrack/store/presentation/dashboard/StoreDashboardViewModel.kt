package ir.siamak.fintrack.store.presentation.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Owns only dashboard UI state; aggregation belongs in injected use cases. */
class StoreDashboardViewModel : ViewModel() { private val mutableState = MutableStateFlow(StoreDashboardUiState()); val state: StateFlow<StoreDashboardUiState> = mutableState.asStateFlow() }

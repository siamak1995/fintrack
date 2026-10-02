package ir.siamak.fintrack.store.presentation.sales.list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Owns sales UI state and delegates data retrieval to use cases when wired by DI. */
class SalesViewModel : ViewModel() { private val mutableState = MutableStateFlow(SalesUiState()); val state: StateFlow<SalesUiState> = mutableState.asStateFlow() }

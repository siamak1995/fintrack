package ir.siamak.fintrack.storeaccountant.presentation.sales.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SalesUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: SalesEvent) {
        // Implementation for loading sales list
    }
}

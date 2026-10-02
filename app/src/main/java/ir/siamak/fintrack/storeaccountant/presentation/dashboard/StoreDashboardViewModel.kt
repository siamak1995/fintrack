package ir.siamak.fintrack.storeaccountant.presentation.dashboard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * مدیریت منطق داشبورد فروشگاه.
 */
@HiltViewModel
class StoreDashboardViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(StoreDashboardUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: StoreDashboardEvent) {
        when (event) {
            is StoreDashboardEvent.Refresh -> {
                // در فازهای بعدی پیاده‌سازی می‌شود
            }
        }
    }
}


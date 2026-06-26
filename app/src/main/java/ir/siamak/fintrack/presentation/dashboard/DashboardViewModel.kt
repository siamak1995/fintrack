package ir.siamak.fintrack.presentation.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


//ViewModel:
//مغز متفکر است. Event را از صفحه می‌گیرد، پردازش می‌کند، و نتیجه را در قالب یک State جدید به صفحه می‌فرستد.

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())

    val state: StateFlow<DashboardState> = _state.asStateFlow()

}
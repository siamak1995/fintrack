package ir.siamak.fintrack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.usecase.wallet.GetAllWalletsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel:
 * مغز متفکر است. Event را از صفحه می‌گیرد، پردازش می‌کند، و نتیجه را در قالب یک State جدید به صفحه می‌فرستد.
 *
 * ویومدل مسئول مدیریت منطق و وضعیت صفحه داشبورد.
 *
 * این کلاس با استفاده از [getAllWalletsUseCase] داده‌ها را دریافت کرده و
 * آن‌ها را به [DashboardState] تبدیل می‌کند تا در رابط کاربری نمایش داده شود.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllWalletsUseCase: GetAllWalletsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        onEvent(DashboardEvent.RefreshData)
    }

    /**
     * مدیریت رویدادهای ورودی از UI.
     *
     * @param event رویدادی که کاربر در رابط کاربری انجام داده است (مثل Refresh).
     */
    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.RefreshData -> loadData()
        }
    }

    /**
     * دریافت لیست کیف ‌پول‌ها از لایه UseCase و آپدیت کردن وضعیت [DashboardState].
     */
    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getAllWalletsUseCase().collect { wallets ->
                _state.update {
                    it.copy(
                        wallets = wallets,
                        totalBalance = wallets.sumOf { w -> w.balance },
                        isLoading = false
                    )
                }
            }
        }
    }
}

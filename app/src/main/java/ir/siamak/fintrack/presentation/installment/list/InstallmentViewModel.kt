package ir.siamak.fintrack.presentation.installment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.usecase.installment.InstallmentUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel مربوط به صفحه لیست اقساط.
 *
 * وظایف:
 * - دریافت اقساط از UseCase
 * - نگهداری state صفحه
 * - مدیریت خطاها
 */
@HiltViewModel
class InstallmentViewModel @Inject constructor(
    private val installmentUseCases: InstallmentUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(InstallmentUiState())
    val state: StateFlow<InstallmentUiState> = _state.asStateFlow()

    init {
        observeInstallments()
    }

    /**
     * دریافت لیست اقساط از دیتابیس به صورت Flow.
     */
    private fun observeInstallments() {
        viewModelScope.launch {
            installmentUseCases.getAllInstallments()
                .collect { list ->
                    _state.value = _state.value.copy(
                        installments = list,
                        isLoading = false
                    )
                }
        }
    }
}

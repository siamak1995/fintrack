package ir.siamak.fintrack.account.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.account.domain.usecase.GetActiveContextUseCase
import ir.siamak.fintrack.account.domain.usecase.GetAvailableContextsUseCase
import ir.siamak.fintrack.account.domain.usecase.SwitchContextUseCase
import ir.siamak.fintrack.common.core.error.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Coordinates context session state while delegating selection rules to domain use cases. */
@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val getAvailableContexts: GetAvailableContextsUseCase,
    private val getActiveContext: GetActiveContextUseCase,
    private val switchContext: SwitchContextUseCase
) : ViewModel() {
    private val mutableState = MutableStateFlow(AppSessionState())
    val state: StateFlow<AppSessionState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(getAvailableContexts(), getActiveContext()) { contexts, active -> contexts to active }
                .collect { (contexts, active) ->
                    mutableState.update { current -> current.copy(availableContexts = contexts, activeContext = active, isLoading = false) }
                }
        }
    }

    /** Handles a shell event without embedding context rules in the UI. */
    fun onEvent(event: AppSessionEvent) {
        when (event) {
            is AppSessionEvent.SwitchContext -> switch(event.contextId)
            AppSessionEvent.ClearError -> mutableState.update { it.copy(error = null) }
        }
    }

    private fun switch(contextId: Long) = viewModelScope.launch {
        mutableState.update { it.copy(isLoading = true, error = null) }
        switchContext(contextId).onFailure {
            mutableState.update { current -> current.copy(isLoading = false, error = AppError.InvalidInput) }
        }
    }
}

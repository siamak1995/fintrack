package ir.siamak.fintrack.presentation.security.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.security.usecase.ObserveSecuritySettingsUseCase
import ir.siamak.fintrack.domain.security.usecase.VerifyPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handles PIN verification and biometric unlock state for the lock screen.
 */
@HiltViewModel
class LockViewModel @Inject constructor(
    observeSecuritySettingsUseCase: ObserveSecuritySettingsUseCase,
    private val verifyPinUseCase: VerifyPinUseCase
) : ViewModel() {

    private val localState = MutableStateFlow(LockState())

    val state: StateFlow<LockState> = combine(
        localState,
        observeSecuritySettingsUseCase()
    ) { state, settings ->
        state.copy(canUseBiometric = settings.canUseBiometric)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LockState()
    )

    fun onEvent(event: LockEvent) {
        when (event) {
            is LockEvent.DigitClicked -> onDigitClicked(event.digit)
            LockEvent.BackspaceClicked -> onBackspaceClicked()
            LockEvent.ClearClicked -> onClearClicked()
            LockEvent.BiometricClicked -> Unit
            LockEvent.BiometricSucceeded -> unlock()
            is LockEvent.BiometricFailed -> showError(event.message)
        }
    }

    private fun onDigitClicked(digit: Int) {
        val currentPin = localState.value.enteredPin
        if (currentPin.length >= PIN_LENGTH) return

        val newPin = currentPin + digit.toString()
        localState.update {
            it.copy(
                enteredPin = newPin,
                errorMessage = null
            )
        }

        if (newPin.length == PIN_LENGTH) {
            verifyPin(newPin)
        }
    }

    private fun onBackspaceClicked() {
        localState.update {
            it.copy(
                enteredPin = it.enteredPin.dropLast(1),
                errorMessage = null
            )
        }
    }

    private fun onClearClicked() {
        localState.update {
            it.copy(
                enteredPin = "",
                errorMessage = null
            )
        }
    }

    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            localState.update { it.copy(isLoading = true) }

            val isValid = verifyPinUseCase(pin)

            localState.update {
                if (isValid) {
                    it.copy(
                        isLoading = false,
                        isUnlocked = true,
                        errorMessage = null
                    )
                } else {
                    it.copy(
                        enteredPin = "",
                        isLoading = false,
                        errorMessage = "رمز وارد شده نادرست است"
                    )
                }
            }
        }
    }

    private fun unlock() {
        localState.update {
            it.copy(
                isUnlocked = true,
                errorMessage = null
            )
        }
    }

    private fun showError(message: String) {
        localState.update {
            it.copy(errorMessage = message)
        }
    }

    private companion object {
        const val PIN_LENGTH = 4
    }
}

package ir.siamak.fintrack.presentation.security.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.security.usecase.SetPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val setPinUseCase: SetPinUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PinSetupState())
    val state = _state.asStateFlow()

    fun onEvent(event: PinSetupEvent) {
        when (event) {
            is PinSetupEvent.DigitClicked -> onDigitClicked(event.digit)
            PinSetupEvent.BackspaceClicked -> onBackspaceClicked()
            PinSetupEvent.Dismiss -> Unit
        }
    }

    private fun onDigitClicked(digit: Int) {
        val currentState = _state.value
        val currentPin = if (currentState.step == PinSetupStep.ENTER_NEW)
            currentState.firstPin else currentState.secondPin

        if (currentPin.length >= 4) return

        val newPin = currentPin + digit.toString()

        if (currentState.step == PinSetupStep.ENTER_NEW) {
            _state.update { it.copy(firstPin = newPin, errorMessage = null) }
            if (newPin.length == 4) {
                _state.update { it.copy(step = PinSetupStep.CONFIRM_NEW) }
            }
        } else {
            _state.update { it.copy(secondPin = newPin, errorMessage = null) }
            if (newPin.length == 4) {
                verifyAndSave(newPin)
            }
        }
    }

    private fun onBackspaceClicked() {
        _state.update {
            if (it.step == PinSetupStep.ENTER_NEW) {
                it.copy(firstPin = it.firstPin.dropLast(1))
            } else {
                it.copy(secondPin = it.secondPin.dropLast(1))
            }
        }
    }

    private fun verifyAndSave(confirmPin: String) {
        if (confirmPin == _state.value.firstPin) {
            viewModelScope.launch {
                setPinUseCase(confirmPin)
                _state.update { it.copy(isFinished = true) }
            }
        } else {
            _state.update {
                it.copy(
                    secondPin = "",
                    errorMessage = "رمزها یکسان نیستند. دوباره امتحان کنید"
                )
            }
        }
    }
}

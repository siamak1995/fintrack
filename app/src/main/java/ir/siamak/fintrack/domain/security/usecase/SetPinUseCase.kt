package ir.siamak.fintrack.domain.security.usecase

import ir.siamak.fintrack.domain.security.repository.SecurityRepository
import javax.inject.Inject

/**
 * Stores a new application PIN after validating its basic format.
 */
class SetPinUseCase @Inject constructor(
    private val repository: SecurityRepository
) {

    suspend operator fun invoke(pin: String) {
        require(pin.length == PIN_LENGTH && pin.all(Char::isDigit)) {
            "PIN must be exactly $PIN_LENGTH digits."
        }

        repository.setPin(pin)
    }

    private companion object {
        const val PIN_LENGTH = 4
    }
}

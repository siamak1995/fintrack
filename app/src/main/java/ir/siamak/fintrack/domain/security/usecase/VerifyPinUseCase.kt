package ir.siamak.fintrack.domain.security.usecase

import ir.siamak.fintrack.domain.security.repository.SecurityRepository
import javax.inject.Inject

/**
 * Verifies a PIN against the securely stored PIN hash.
 */
class VerifyPinUseCase @Inject constructor(
    private val repository: SecurityRepository
) {

    suspend operator fun invoke(pin: String): Boolean {
        if (pin.length != PIN_LENGTH || !pin.all(Char::isDigit)) {
            return false
        }

        return repository.verifyPin(pin)
    }

    private companion object {
        const val PIN_LENGTH = 4
    }
}

package ir.siamak.fintrack.domain.security.usecase

import ir.siamak.fintrack.domain.security.repository.SecurityRepository
import javax.inject.Inject

/**
 * Removes the stored PIN and disables biometric authentication.
 */
class ClearPinUseCase @Inject constructor(
    private val repository: SecurityRepository
) {

    suspend operator fun invoke() {
        repository.clearPin()
    }
}

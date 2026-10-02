package ir.siamak.fintrack.personalaccountant.domain.security.usecase

import ir.siamak.fintrack.personalaccountant.domain.security.repository.SecurityRepository
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


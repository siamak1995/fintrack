package ir.siamak.fintrack.domain.security.usecase

import ir.siamak.fintrack.domain.security.repository.SecurityRepository
import javax.inject.Inject

/**
 * Enables or disables biometric authentication.
 */
class SetBiometricEnabledUseCase @Inject constructor(
    private val repository: SecurityRepository
) {

    suspend operator fun invoke(enabled: Boolean) {
        repository.setBiometricEnabled(enabled)
    }
}

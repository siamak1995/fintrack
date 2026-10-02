package ir.siamak.fintrack.personalaccountant.domain.security.usecase

import ir.siamak.fintrack.personalaccountant.domain.security.repository.SecurityRepository
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


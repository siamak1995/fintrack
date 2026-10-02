package ir.siamak.fintrack.personalaccountant.domain.security.usecase

import ir.siamak.fintrack.personalaccountant.domain.security.model.SecuritySettings
import ir.siamak.fintrack.personalaccountant.domain.security.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observes local application security settings.
 */
class ObserveSecuritySettingsUseCase @Inject constructor(
    private val repository: SecurityRepository
) {

    operator fun invoke(): Flow<SecuritySettings> {
        return repository.observeSecuritySettings()
    }
}


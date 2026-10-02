package ir.siamak.fintrack.personalaccountant.domain.settings

import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(): AppSettings {
        return repository.getSettings()
    }
}


package ir.siamak.fintrack.personalaccountant.domain.settings

import javax.inject.Inject

class SaveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(settings: AppSettings) {
        repository.saveSettings(settings)
    }
}


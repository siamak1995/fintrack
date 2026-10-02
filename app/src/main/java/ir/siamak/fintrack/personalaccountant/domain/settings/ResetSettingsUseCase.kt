package ir.siamak.fintrack.personalaccountant.domain.settings

import javax.inject.Inject

class ResetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        repository.resetSettings()
    }
}


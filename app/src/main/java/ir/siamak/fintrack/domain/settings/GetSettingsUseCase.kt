package ir.siamak.fintrack.domain.settings

import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(): AppSettings {
        return repository.getSettings()
    }
}

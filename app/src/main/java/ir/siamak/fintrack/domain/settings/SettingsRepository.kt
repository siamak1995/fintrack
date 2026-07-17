package ir.siamak.fintrack.domain.settings

interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    suspend fun saveSettings(settings: AppSettings)
    suspend fun resetSettings()
}

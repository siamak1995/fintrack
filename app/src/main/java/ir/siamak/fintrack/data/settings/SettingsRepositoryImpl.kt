package ir.siamak.fintrack.data.settings

import ir.siamak.fintrack.domain.settings.AppSettings
import ir.siamak.fintrack.domain.settings.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * پیاده‌سازی Repository تنظیمات.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsPreferenceDataSource
) : SettingsRepository {

    override suspend fun getSettings(): AppSettings = dataSource.getSettings()

    override suspend fun saveSettings(settings: AppSettings) {
        dataSource.saveSettings(settings)
    }

    override suspend fun resetSettings() {
        dataSource.resetSettings()
    }
}

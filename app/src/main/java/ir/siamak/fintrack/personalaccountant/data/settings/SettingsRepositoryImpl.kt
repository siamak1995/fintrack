package ir.siamak.fintrack.personalaccountant.data.settings

import ir.siamak.fintrack.personalaccountant.domain.settings.AppSettings
import ir.siamak.fintrack.personalaccountant.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * پیاده‌سازی مخزن تنظیمات برنامه.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsPreferenceDataSource
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> {
        return dataSource.observeSettings()
    }

    override suspend fun getSettings(): AppSettings {
        return dataSource.getSettings()
    }

    override suspend fun saveSettings(settings: AppSettings) {
        dataSource.saveSettings(settings)
    }

    override suspend fun resetSettings() {
        dataSource.resetSettings()
    }
}


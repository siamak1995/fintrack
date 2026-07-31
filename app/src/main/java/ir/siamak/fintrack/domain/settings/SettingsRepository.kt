package ir.siamak.fintrack.domain.settings

import kotlinx.coroutines.flow.Flow

/**
 * قرارداد مخزن تنظیمات برنامه.
 */
interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun getSettings(): AppSettings
    suspend fun saveSettings(settings: AppSettings)
    suspend fun resetSettings()
}

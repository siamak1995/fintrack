package ir.siamak.fintrack.data.settings

import ir.siamak.fintrack.domain.settings.AppSettings
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferenceDataSource @Inject constructor() {

    private val mutex = Mutex()
    private var settings: AppSettings = AppSettings()

    suspend fun getSettings(): AppSettings {
        return mutex.withLock { settings }
    }

    suspend fun saveSettings(newSettings: AppSettings) {
        mutex.withLock {
            settings = newSettings
        }
    }

    suspend fun resetSettings() {
        mutex.withLock {
            settings = AppSettings()
        }
    }
}

package ir.siamak.fintrack.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.domain.settings.AppSettings
import ir.siamak.fintrack.presentation.settings.AppLanguage
import ir.siamak.fintrack.presentation.settings.FirstDayOfWeek
import ir.siamak.fintrack.presentation.theme.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

/**
 * منبع داده تنظیمات مبتنی بر DataStore.
 *
 * این کلاس تنظیمات را به‌صورت پایدار در حافظه دائمی ذخیره می‌کند
 * تا بعد از بسته شدن برنامه نیز باقی بمانند.
 */
@Singleton
class SettingsPreferenceDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val theme = stringPreferencesKey("theme")
        val currency = stringPreferencesKey("currency")
        val language = stringPreferencesKey("language")
        val biometricEnabled = booleanPreferencesKey("biometric_enabled")
        val pinEnabled = booleanPreferencesKey("pin_enabled")
        val notificationEnabled = booleanPreferencesKey("notification_enabled")
        val installmentReminder = booleanPreferencesKey("installment_reminder")
        val dailyReminder = booleanPreferencesKey("daily_reminder")
        val budgetReminder = booleanPreferencesKey("budget_reminder")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
        val firstDayOfWeek = stringPreferencesKey("first_day_of_week")
    }

    suspend fun getSettings(): AppSettings {
        return context.settingsDataStore.data
            .map { prefs -> prefs.toAppSettings() }
            .first()
    }

    suspend fun saveSettings(newSettings: AppSettings) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.theme] = newSettings.theme.name
            prefs[Keys.currency] = newSettings.currency.name
            prefs[Keys.language] = newSettings.language.name
            prefs[Keys.biometricEnabled] = newSettings.biometricEnabled
            prefs[Keys.pinEnabled] = newSettings.pinEnabled
            prefs[Keys.notificationEnabled] = newSettings.notificationEnabled
            prefs[Keys.installmentReminder] = newSettings.installmentReminder
            prefs[Keys.dailyReminder] = newSettings.dailyReminder
            prefs[Keys.budgetReminder] = newSettings.budgetReminder
            prefs[Keys.dynamicColor] = newSettings.dynamicColor
            prefs[Keys.firstDayOfWeek] = newSettings.firstDayOfWeek.name
        }
    }

    suspend fun resetSettings() {
        context.settingsDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun Preferences.toAppSettings(): AppSettings {
        return AppSettings(
            theme = ThemeMode.entries.firstOrNull { it.name == this[Keys.theme] } ?: ThemeMode.SYSTEM,
            currency = Currency.entries.firstOrNull { it.name == this[Keys.currency] } ?: Currency.TOMAN,
            language = AppLanguage.entries.firstOrNull { it.name == this[Keys.language] } ?: AppLanguage.PERSIAN,
            biometricEnabled = this[Keys.biometricEnabled] ?: false,
            pinEnabled = this[Keys.pinEnabled] ?: false,
            notificationEnabled = this[Keys.notificationEnabled] ?: true,
            installmentReminder = this[Keys.installmentReminder] ?: true,
            dailyReminder = this[Keys.dailyReminder] ?: false,
            budgetReminder = this[Keys.budgetReminder] ?: true,
            dynamicColor = this[Keys.dynamicColor] ?: true,
            firstDayOfWeek = FirstDayOfWeek.entries.firstOrNull {
                it.name == this[Keys.firstDayOfWeek]
            } ?: FirstDayOfWeek.SATURDAY
        )
    }
}

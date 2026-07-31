package ir.siamak.fintrack.data.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import ir.siamak.fintrack.domain.security.model.SecuritySettings
import ir.siamak.fintrack.domain.security.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SecurityRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val pinHasher: PinHasher
) : SecurityRepository {

    override fun observeSecuritySettings(): Flow<SecuritySettings> {
        return dataStore.data.map { preferences ->
            val pinHash = preferences[SecurityPreferencesKeys.PIN_HASH]
            SecuritySettings(
                isPinEnabled = preferences[SecurityPreferencesKeys.IS_PIN_ENABLED] ?: false,
                isBiometricEnabled = preferences[SecurityPreferencesKeys.IS_BIOMETRIC_ENABLED] ?: false,
                hasPin = !pinHash.isNullOrEmpty()
            )
        }
    }

    override suspend fun setPin(pin: String) {
        val hash = pinHasher.hash(pin)
        dataStore.edit { preferences ->
            preferences[SecurityPreferencesKeys.PIN_HASH] = hash
            preferences[SecurityPreferencesKeys.IS_PIN_ENABLED] = true
        }
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val preferences = dataStore.data.first()
        val storedHash = preferences[SecurityPreferencesKeys.PIN_HASH] ?: return false
        return pinHasher.verify(pin, storedHash)
    }

    override suspend fun clearPin() {
        dataStore.edit { preferences ->
            preferences.remove(SecurityPreferencesKeys.PIN_HASH)
            preferences[SecurityPreferencesKeys.IS_PIN_ENABLED] = false
            preferences[SecurityPreferencesKeys.IS_BIOMETRIC_ENABLED] = false
        }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SecurityPreferencesKeys.IS_BIOMETRIC_ENABLED] = enabled
        }
    }
}

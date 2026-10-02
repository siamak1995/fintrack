package ir.siamak.fintrack.personalaccountant.data.security

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore keys for security preferences.
 */
object SecurityPreferencesKeys {
    val PIN_HASH = stringPreferencesKey("pin_hash")
    val IS_PIN_ENABLED = booleanPreferencesKey("is_pin_enabled")
    val IS_BIOMETRIC_ENABLED = booleanPreferencesKey("is_biometric_enabled")
}


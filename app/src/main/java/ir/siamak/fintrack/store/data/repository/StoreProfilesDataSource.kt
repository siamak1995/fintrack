package ir.siamak.fintrack.store.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import ir.siamak.fintrack.store.domain.model.*
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/** Uses the existing, backed-up application Preferences; never opens a second DataStore instance. */
@Singleton
class StoreProfilesDataSource @Inject constructor(private val preferences: DataStore<Preferences>) {
    private val key = stringPreferencesKey("store_profiles_v1")
    val profiles = preferences.data.map { values -> values[key]?.let { Json.decodeFromString<StoreProfiles>(it) } ?: StoreProfiles() }
    suspend fun update(change: (StoreProfiles) -> StoreProfiles) {
        preferences.edit { values ->
            val current = values[key]?.let { Json.decodeFromString<StoreProfiles>(it) } ?: StoreProfiles()
            values[key] = Json.encodeToString(change(current))
        }
    }
}

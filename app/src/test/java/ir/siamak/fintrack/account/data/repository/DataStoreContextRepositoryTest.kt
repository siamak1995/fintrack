package ir.siamak.fintrack.account.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/** Verifies persisted selection and safe fallback when a stored context is no longer valid. */
class DataStoreContextRepositoryTest {
    @Test fun persistsSelectionForANewRepositoryInstance() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) { Files.createTempFile("fintrack-context", ".preferences_pb").toFile() }
        try {
            DataStoreContextRepository(dataStore).switchActiveContext(STORE_CONTEXT_ID)
            assertEquals(STORE_CONTEXT_ID, DataStoreContextRepository(dataStore).observeActiveContext().first()?.id)
        } finally {
            scope.cancel()
        }
    }

    @Test fun fallsBackWhenPersistedContextIsUnavailable() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) { Files.createTempFile("fintrack-context", ".preferences_pb").toFile() }
        try {
            dataStore.edit { preferences -> preferences[ACTIVE_CONTEXT_KEY] = INVALID_CONTEXT_ID }
            assertEquals(PERSONAL_CONTEXT_ID, DataStoreContextRepository(dataStore).observeActiveContext().first()?.id)
        } finally {
            scope.cancel()
        }
    }

    private companion object {
        val ACTIVE_CONTEXT_KEY = longPreferencesKey("active_accountant_context_id")
        const val PERSONAL_CONTEXT_ID = 1L
        const val STORE_CONTEXT_ID = 2L
        const val INVALID_CONTEXT_ID = 99L
    }
}

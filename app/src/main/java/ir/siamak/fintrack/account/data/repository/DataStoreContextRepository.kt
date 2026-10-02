package ir.siamak.fintrack.account.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.account.domain.model.AccountantType
import ir.siamak.fintrack.account.domain.repository.ContextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Persists the selected system context in DataStore while Room context tables are introduced later. */
class DataStoreContextRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ContextRepository {
    private val contexts = flowOf(defaultContexts())

    override fun observeAvailableContexts(): Flow<List<AccountantContext>> = contexts

    override fun observeActiveContext(): Flow<AccountantContext?> = combine(
        contexts,
        dataStore.data
    ) { availableContexts, preferences ->
        val requestedId = preferences[Keys.ACTIVE_CONTEXT_ID]
        availableContexts.firstOrNull { it.id == requestedId && it.isActive }
            ?: availableContexts.firstOrNull { it.isActive }
    }

    override suspend fun switchActiveContext(contextId: Long): Result<AccountantContext> = runCatching {
        val selected = defaultContexts().firstOrNull { it.id == contextId }
            ?: error("Context was not found")
        check(selected.isActive) { "Context is inactive" }
        dataStore.edit { preferences -> preferences[Keys.ACTIVE_CONTEXT_ID] = selected.id }
        selected
    }

    private fun defaultContexts(): List<AccountantContext> = listOf(
        AccountantContext(
            id = PERSONAL_CONTEXT_ID,
            ownerUserId = LOCAL_OWNER_ID,
            type = AccountantType.PERSONAL,
            name = "حسابدار شخصی",
            isActive = true,
            createdAt = SYSTEM_CONTEXT_TIMESTAMP,
            updatedAt = SYSTEM_CONTEXT_TIMESTAMP
        ),
        AccountantContext(
            id = STORE_CONTEXT_ID,
            ownerUserId = LOCAL_OWNER_ID,
            type = AccountantType.STORE,
            name = "حسابدار غرفه",
            isActive = true,
            createdAt = SYSTEM_CONTEXT_TIMESTAMP,
            updatedAt = SYSTEM_CONTEXT_TIMESTAMP
        )
    )

    private object Keys { val ACTIVE_CONTEXT_ID = longPreferencesKey("active_accountant_context_id") }

    private companion object {
        const val LOCAL_OWNER_ID = 0L
        const val PERSONAL_CONTEXT_ID = 1L
        const val STORE_CONTEXT_ID = 2L
        const val SYSTEM_CONTEXT_TIMESTAMP = 0L
    }
}

package ir.siamak.fintrack.account.data.repository

import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.account.domain.repository.ContextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/** Temporary process-lifetime context store used before the DataStore adapter is introduced. */
class InMemoryContextRepository(initialContexts: List<AccountantContext>) : ContextRepository {
    private val contexts = MutableStateFlow(initialContexts)
    private val activeContextId = MutableStateFlow(initialContexts.singleOrNull { it.isActive }?.id)

    override fun observeAvailableContexts(): StateFlow<List<AccountantContext>> = contexts.asStateFlow()

    override fun observeActiveContext(): Flow<AccountantContext?> = activeContextId.map { activeId ->
        contexts.value.firstOrNull { it.id == activeId && it.isActive }
    }

    override suspend fun switchActiveContext(contextId: Long): Result<AccountantContext> = runCatching {
        val selected = contexts.value.firstOrNull { it.id == contextId }
            ?: error("Context was not found")
        check(selected.isActive) { "Context is inactive" }
        activeContextId.value = selected.id
        selected
    }
}

package ir.siamak.fintrack.account.domain.repository

import ir.siamak.fintrack.account.domain.model.AccountantContext
import kotlinx.coroutines.flow.Flow

/** Data boundary for available accountant contexts and their active selection. */
interface ContextRepository {
    fun observeAvailableContexts(): Flow<List<AccountantContext>>
    fun observeActiveContext(): Flow<AccountantContext?>
    suspend fun switchActiveContext(contextId: Long): Result<AccountantContext>
}

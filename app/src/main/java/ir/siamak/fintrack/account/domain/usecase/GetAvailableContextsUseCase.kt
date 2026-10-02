package ir.siamak.fintrack.account.domain.usecase

import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.account.domain.repository.ContextRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Streams contexts the signed-in user is permitted to open. */
class GetAvailableContextsUseCase @Inject constructor(private val repository: ContextRepository) {
    operator fun invoke(): Flow<List<AccountantContext>> = repository.observeAvailableContexts()
}

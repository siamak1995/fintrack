package ir.siamak.fintrack.account.domain.usecase

import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.account.domain.repository.ContextRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Streams the current active accountant context. */
class GetActiveContextUseCase @Inject constructor(private val repository: ContextRepository) {
    operator fun invoke(): Flow<AccountantContext?> = repository.observeActiveContext()
}

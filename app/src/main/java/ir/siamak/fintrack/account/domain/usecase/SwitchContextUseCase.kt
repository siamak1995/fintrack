package ir.siamak.fintrack.account.domain.usecase

import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.account.domain.repository.ContextRepository
import javax.inject.Inject

/** Validates and selects an available, active accountant context. */
class SwitchContextUseCase @Inject constructor(private val repository: ContextRepository) {
    suspend operator fun invoke(contextId: Long): Result<AccountantContext> {
        require(contextId > 0) { "Context identifier must be positive" }
        return repository.switchActiveContext(contextId)
    }
}

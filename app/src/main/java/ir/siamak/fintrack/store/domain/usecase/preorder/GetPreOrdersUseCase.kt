package ir.siamak.fintrack.store.domain.usecase.preorder

import ir.siamak.fintrack.store.domain.model.PreOrder
import ir.siamak.fintrack.store.domain.repository.PreOrderRepository
import kotlinx.coroutines.flow.Flow

/** Streams custom pre-orders. */
class GetPreOrdersUseCase(private val repository: PreOrderRepository) { operator fun invoke(): Flow<List<PreOrder>> = repository.observeAll() }

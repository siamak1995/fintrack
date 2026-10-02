package ir.siamak.fintrack.store.domain.usecase.preorder

import ir.siamak.fintrack.store.domain.model.PreOrder
import ir.siamak.fintrack.store.domain.repository.PreOrderRepository

/** Persists a valid custom pre-order. */
class CreatePreOrderUseCase(private val repository: PreOrderRepository) { suspend operator fun invoke(order: PreOrder): Long { require(order.customerId > 0 && order.description.isNotBlank() && order.agreedPrice >= 0); return repository.create(order) } }

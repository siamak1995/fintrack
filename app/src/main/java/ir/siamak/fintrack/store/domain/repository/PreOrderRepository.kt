package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.PreOrder
import kotlinx.coroutines.flow.Flow

/** Custom pre-order data boundary. */
interface PreOrderRepository { fun observeAll(): Flow<List<PreOrder>>; suspend fun create(order: PreOrder): Long; suspend fun update(order: PreOrder) }

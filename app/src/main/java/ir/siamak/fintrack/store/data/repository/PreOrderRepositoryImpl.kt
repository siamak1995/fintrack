package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.PreOrderDao
import ir.siamak.fintrack.store.data.mapper.toDomain
import ir.siamak.fintrack.store.data.mapper.toEntity
import ir.siamak.fintrack.store.domain.model.PreOrder
import ir.siamak.fintrack.store.domain.repository.PreOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation of [PreOrderRepository]. */
class PreOrderRepositoryImpl(private val dao: PreOrderDao) : PreOrderRepository { override fun observeAll(): Flow<List<PreOrder>> = dao.observeAll().map { it.map { entity -> entity.toDomain() } }; override suspend fun create(order: PreOrder): Long = dao.insert(order.toEntity()); override suspend fun update(order: PreOrder) = dao.update(order.toEntity()) }

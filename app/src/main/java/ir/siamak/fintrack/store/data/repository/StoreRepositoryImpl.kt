package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.StoreDao
import ir.siamak.fintrack.store.data.mapper.toDomain
import ir.siamak.fintrack.store.data.mapper.toEntity
import ir.siamak.fintrack.store.domain.model.Store
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation of [StoreRepository]. */
class StoreRepositoryImpl(private val dao: StoreDao) : StoreRepository { override fun observeStore(): Flow<Store?> = dao.observe().map { it?.toDomain() }; override suspend fun save(store: Store) = dao.upsert(store.toEntity()) }

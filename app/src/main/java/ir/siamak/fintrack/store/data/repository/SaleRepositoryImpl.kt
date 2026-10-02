package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.SaleDao
import ir.siamak.fintrack.store.data.mapper.toDomain
import ir.siamak.fintrack.store.data.mapper.toEntity
import ir.siamak.fintrack.store.domain.model.Sale
import ir.siamak.fintrack.store.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation of [SaleRepository]. */
class SaleRepositoryImpl(private val dao: SaleDao) : SaleRepository { override fun observeAll(): Flow<List<Sale>> = dao.observeAll().map { it.map { entity -> entity.toDomain() } }; override suspend fun create(sale: Sale): Long = dao.insert(sale.toEntity()) }

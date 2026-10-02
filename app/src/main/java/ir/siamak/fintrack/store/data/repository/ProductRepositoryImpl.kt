package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.ProductDao
import ir.siamak.fintrack.store.data.mapper.toDomain
import ir.siamak.fintrack.store.data.mapper.toEntity
import ir.siamak.fintrack.store.domain.model.Product
import ir.siamak.fintrack.store.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation of [ProductRepository]. */
class ProductRepositoryImpl(private val dao: ProductDao) : ProductRepository { override fun observeAll(): Flow<List<Product>> = dao.observeAll().map { it.map { entity -> entity.toDomain() } }; override suspend fun save(product: Product) = dao.upsert(product.toEntity()); override suspend fun delete(id: Long) = dao.delete(id) }

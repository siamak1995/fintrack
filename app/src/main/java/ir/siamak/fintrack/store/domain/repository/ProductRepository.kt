package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.Product
import kotlinx.coroutines.flow.Flow

/** Product catalog data boundary. */
interface ProductRepository { fun observeAll(): Flow<List<Product>>; suspend fun save(product: Product); suspend fun delete(id: Long) }

package ir.siamak.fintrack.store.domain.usecase.product

import ir.siamak.fintrack.store.domain.model.Product
import ir.siamak.fintrack.store.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

/** Streams the product catalog. */
class GetProductsUseCase(private val repository: ProductRepository) { operator fun invoke(): Flow<List<Product>> = repository.observeAll() }

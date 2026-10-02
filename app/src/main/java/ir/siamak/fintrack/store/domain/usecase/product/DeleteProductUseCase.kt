package ir.siamak.fintrack.store.domain.usecase.product

import ir.siamak.fintrack.store.domain.repository.ProductRepository

/** Deletes a product by identifier. */
class DeleteProductUseCase(private val repository: ProductRepository) { suspend operator fun invoke(id: Long) { require(id > 0); repository.delete(id) } }

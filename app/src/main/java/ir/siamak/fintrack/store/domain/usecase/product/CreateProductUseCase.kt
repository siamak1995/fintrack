package ir.siamak.fintrack.store.domain.usecase.product

import ir.siamak.fintrack.store.domain.model.Product
import ir.siamak.fintrack.store.domain.repository.ProductRepository

/** Validates and saves a product. */
class CreateProductUseCase(private val repository: ProductRepository) { suspend operator fun invoke(product: Product) { require(product.name.isNotBlank()) { "Product name is required" }; require(product.stock >= 0 && product.price >= 0); repository.save(product) } }

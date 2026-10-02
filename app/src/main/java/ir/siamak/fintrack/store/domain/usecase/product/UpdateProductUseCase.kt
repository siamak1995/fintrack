package ir.siamak.fintrack.store.domain.usecase.product

import ir.siamak.fintrack.store.domain.model.Product
import ir.siamak.fintrack.store.domain.repository.ProductRepository

/** Updates an existing product. */
class UpdateProductUseCase(private val repository: ProductRepository) { suspend operator fun invoke(product: Product) { require(product.id > 0 && product.name.isNotBlank()); repository.save(product) } }

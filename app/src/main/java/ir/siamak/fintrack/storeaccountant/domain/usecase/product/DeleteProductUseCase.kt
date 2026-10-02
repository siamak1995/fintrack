package ir.siamak.fintrack.storeaccountant.domain.usecase.product

import ir.siamak.fintrack.storeaccountant.domain.model.Product
import ir.siamak.fintrack.storeaccountant.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) = repository.deleteProduct(product)
}

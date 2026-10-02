package ir.siamak.fintrack.storeaccountant.domain.usecase.product

import ir.siamak.fintrack.storeaccountant.domain.model.Product
import ir.siamak.fintrack.storeaccountant.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.getAllProducts()
}

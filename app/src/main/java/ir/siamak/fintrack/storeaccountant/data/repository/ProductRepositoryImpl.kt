package ir.siamak.fintrack.storeaccountant.data.repository

import ir.siamak.fintrack.storeaccountant.data.dao.ProductDao
import ir.siamak.fintrack.storeaccountant.data.mapper.toDomain
import ir.siamak.fintrack.storeaccountant.data.mapper.toEntity
import ir.siamak.fintrack.storeaccountant.domain.model.Product
import ir.siamak.fintrack.storeaccountant.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao
) : ProductRepository {
    override fun getAllProducts(): Flow<List<Product>> {
        return dao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertProduct(product: Product) {
        dao.insertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) {
        dao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        dao.deleteProduct(product.toEntity())
    }
}

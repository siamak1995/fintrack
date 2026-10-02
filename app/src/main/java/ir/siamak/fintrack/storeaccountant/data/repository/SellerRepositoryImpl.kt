package ir.siamak.fintrack.storeaccountant.data.repository

import ir.siamak.fintrack.storeaccountant.data.dao.SellerDao
import ir.siamak.fintrack.storeaccountant.data.mapper.toDomain
import ir.siamak.fintrack.storeaccountant.data.mapper.toEntity
import ir.siamak.fintrack.storeaccountant.domain.model.Seller
import ir.siamak.fintrack.storeaccountant.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val dao: SellerDao
) : SellerRepository {
    override fun getAllSellers(): Flow<List<Seller>> {
        return dao.getAllSellers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSeller(seller: Seller) {
        dao.insertSeller(seller.toEntity())
    }

    override suspend fun updateSeller(seller: Seller) {
        dao.updateSeller(seller.toEntity())
    }

    override suspend fun deleteSeller(seller: Seller) {
        dao.deleteSeller(seller.toEntity())
    }
}

package ir.siamak.fintrack.storeaccountant.domain.repository

import ir.siamak.fintrack.storeaccountant.domain.model.Seller
import kotlinx.coroutines.flow.Flow

interface SellerRepository {
    fun getAllSellers(): Flow<List<Seller>>
    suspend fun insertSeller(seller: Seller)
    suspend fun updateSeller(seller: Seller)
    suspend fun deleteSeller(seller: Seller)
}

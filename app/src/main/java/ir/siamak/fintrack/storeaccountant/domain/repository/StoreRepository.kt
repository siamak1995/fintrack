package ir.siamak.fintrack.storeaccountant.domain.repository

import ir.siamak.fintrack.storeaccountant.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getStore(): Flow<Store?>
    suspend fun saveStore(store: Store)
}

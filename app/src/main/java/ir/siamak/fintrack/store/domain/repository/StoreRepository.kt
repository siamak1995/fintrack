package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.Store
import kotlinx.coroutines.flow.Flow

/** Store profile data boundary, independent from persistence technology. */
interface StoreRepository {
    fun observeStore(): Flow<Store?>
    suspend fun save(store: Store)
}

package ir.siamak.fintrack.storeaccountant.data.repository

import ir.siamak.fintrack.storeaccountant.data.dao.StoreDao
import ir.siamak.fintrack.storeaccountant.data.mapper.toDomain
import ir.siamak.fintrack.storeaccountant.data.mapper.toEntity
import ir.siamak.fintrack.storeaccountant.domain.model.Store
import ir.siamak.fintrack.storeaccountant.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val dao: StoreDao
) : StoreRepository {
    override fun getStore(): Flow<Store?> {
        return dao.getStore().map { it?.toDomain() }
    }

    override suspend fun saveStore(store: Store) {
        dao.insertStore(store.toEntity())
    }
}

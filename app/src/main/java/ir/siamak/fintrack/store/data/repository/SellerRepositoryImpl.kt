package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.SellerDao
import ir.siamak.fintrack.store.data.entity.SellerEntity
import ir.siamak.fintrack.store.domain.model.Seller
import ir.siamak.fintrack.store.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation of [SellerRepository]. */
class SellerRepositoryImpl(private val dao: SellerDao) : SellerRepository {
    override fun observeAll(): Flow<List<Seller>> = dao.observeAll().map { list -> list.map { Seller(it.id, it.firstName, it.lastName, it.phone, it.address, it.description) } }
    override suspend fun save(seller: Seller) = dao.upsert(SellerEntity(seller.id, seller.firstName, seller.lastName, seller.phone, seller.address, seller.description))
    override suspend fun delete(id: Long) = dao.delete(id)
}

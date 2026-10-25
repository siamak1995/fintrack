package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.SellerDao
import ir.siamak.fintrack.store.data.entity.SellerEntity
import ir.siamak.fintrack.store.domain.model.Seller
import ir.siamak.fintrack.store.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

/** Room-backed implementation of [SellerRepository]. */
class SellerRepositoryImpl(private val dao: SellerDao, private val profiles: StoreProfilesDataSource? = null) : SellerRepository {
    override fun observeAll(): Flow<List<Seller>> = combine(dao.observeAll(), profiles?.profiles ?: kotlinx.coroutines.flow.flowOf(ir.siamak.fintrack.store.domain.model.StoreProfiles())) { list, settings ->
        list.map { Seller(it.id, it.firstName, it.lastName, it.phone, it.address, it.description, settings.signatures[it.id], settings.cards[it.id], settings.accounts[it.id]) }
    }
    override suspend fun save(seller: Seller) {
        val inserted = dao.upsert(SellerEntity(seller.id, seller.firstName, seller.lastName, seller.phone, seller.address, seller.description))
        val id = seller.id.takeIf { it > 0 } ?: inserted
        profiles?.update { it.copy(
            signatures = it.signatures - id + listOfNotNull(seller.signaturePath?.let { path -> id to path }).toMap(),
            cards = it.cards - id + listOfNotNull(seller.cardNumber?.let { number -> id to number }).toMap(),
            accounts = it.accounts - id + listOfNotNull(seller.accountNumber?.let { number -> id to number }).toMap()) }
    }
    override suspend fun delete(id: Long) = dao.delete(id)
}

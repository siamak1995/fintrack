package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.Seller
import kotlinx.coroutines.flow.Flow

/** Seller data boundary. */
interface SellerRepository { fun observeAll(): Flow<List<Seller>>; suspend fun save(seller: Seller); suspend fun delete(id: Long) }

package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.*
import kotlinx.coroutines.flow.Flow

/** Store profile and atomic checkout boundary. No personal-accounting data enters this boundary. */
interface StoreRepository {
    fun observeStore(): Flow<Store?>
    suspend fun save(store: Store)
    suspend fun checkout(sale: Sale, invoice: Invoice, requestId: String): Long
    suspend fun placeOrder(order: PreOrder, invoice: Invoice, requestId: String): Long
    suspend fun changeOrderStatus(orderId: Long, status: PreOrderStatus)
    suspend fun receivePayment(invoiceId: Long, amount: Long)
}

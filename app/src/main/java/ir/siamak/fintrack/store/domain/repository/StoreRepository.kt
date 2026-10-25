package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Store checkout is atomic locally. Optional personal receipts use a retryable explicit bridge. */
interface StoreRepository {
    fun observeStore(): Flow<Store?>
    fun observeStores(): Flow<List<Store>> = observeStore().map { listOfNotNull(it) }
    fun observeProfiles(): Flow<StoreProfiles> = kotlinx.coroutines.flow.flowOf(StoreProfiles())
    fun observeFinanceDirectory(): Flow<StoreFinanceDirectory> = kotlinx.coroutines.flow.flowOf(StoreFinanceDirectory())
    suspend fun syncInvoice(id: Long) { error("اتصال کیف پول در دسترس نیست") }
    suspend fun selectStore(id: Long) { error("انتخاب فروشگاه پشتیبانی نمی‌شود") }
    suspend fun deleteStore(id: Long) { error("حذف فروشگاه پشتیبانی نمی‌شود") }
    suspend fun saveBrand(brand: Brand) { error("ثبت برند پشتیبانی نمی‌شود") }
    suspend fun deleteBrand(id: String) { error("حذف برند پشتیبانی نمی‌شود") }
    suspend fun save(store: Store)
    suspend fun checkout(sale: Sale, invoice: Invoice, requestId: String): Long
    suspend fun placeOrder(order: PreOrder, invoice: Invoice, requestId: String): Long
    suspend fun changeOrderStatus(orderId: Long, status: PreOrderStatus)
    suspend fun receivePayment(invoiceId: Long, amount: Long)
}

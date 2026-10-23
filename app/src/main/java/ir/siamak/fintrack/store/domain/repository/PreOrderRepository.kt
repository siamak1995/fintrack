package ir.siamak.fintrack.store.domain.repository
import ir.siamak.fintrack.store.domain.model.*
import kotlinx.coroutines.flow.Flow
interface PreOrderRepository {
    fun observeAll(): Flow<List<PreOrder>>
    suspend fun create(order: PreOrder, invoice: Invoice, requestId: String): Long
    suspend fun updateStatus(id: Long, status: PreOrderStatus)
}

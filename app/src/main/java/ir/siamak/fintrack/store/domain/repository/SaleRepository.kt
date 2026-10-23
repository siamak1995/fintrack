package ir.siamak.fintrack.store.domain.repository
import ir.siamak.fintrack.store.domain.model.*
import kotlinx.coroutines.flow.Flow
interface SaleRepository {
    fun observeAll(): Flow<List<Sale>>
    suspend fun create(sale: Sale, invoice: Invoice, requestId: String): Long
}

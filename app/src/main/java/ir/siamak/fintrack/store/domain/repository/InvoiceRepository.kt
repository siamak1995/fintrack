package ir.siamak.fintrack.store.domain.repository
import ir.siamak.fintrack.store.domain.model.Invoice
import kotlinx.coroutines.flow.Flow
/** Read boundary for immutable invoice snapshots and subsequent receipts. */
interface InvoiceRepository {
    fun observeAll(): Flow<List<Invoice>>
}

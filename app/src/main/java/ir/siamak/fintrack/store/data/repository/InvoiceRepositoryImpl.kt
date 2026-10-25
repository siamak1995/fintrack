package ir.siamak.fintrack.store.data.repository
import ir.siamak.fintrack.store.data.dao.InvoiceDao
import ir.siamak.fintrack.store.domain.model.Invoice
import ir.siamak.fintrack.store.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class InvoiceRepositoryImpl(private val dao: InvoiceDao) : InvoiceRepository {
    override fun observeAll() = dao.observeAll().map { records -> records.map { Json.decodeFromString<Invoice>(it.payload).copy(id = it.id, saleId = it.saleId, preOrderId = it.preOrderId) } }
}

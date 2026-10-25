package ir.siamak.fintrack.store.data.repository
import ir.siamak.fintrack.store.data.dao.InvoiceDao
import ir.siamak.fintrack.store.domain.model.Invoice
import ir.siamak.fintrack.store.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class InvoiceRepositoryImpl(private val dao: InvoiceDao) : InvoiceRepository {
    override fun observeAll() = dao.observeAll().map { records ->
        val sequences = records.groupBy { it.preOrderId != null }.values.flatMap { group -> group.sortedBy { it.id }.mapIndexed { index, row -> row.id to index+1L } }.toMap()
        records.map { val invoice = Json.decodeFromString<Invoice>(it.payload)
            invoice.copy(id = it.id, saleId = it.saleId, preOrderId = it.preOrderId, displaySequence = invoice.displaySequence.takeIf { n -> n > 0 } ?: sequences.getValue(it.id)) }
    }
}

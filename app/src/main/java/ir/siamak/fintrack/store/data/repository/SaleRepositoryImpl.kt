package ir.siamak.fintrack.store.data.repository
import ir.siamak.fintrack.store.data.dao.SaleDao
import ir.siamak.fintrack.store.data.mapper.toDomain
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.repository.SaleRepository
import kotlinx.coroutines.flow.map

class SaleRepositoryImpl(private val dao: SaleDao, private val store: ir.siamak.fintrack.store.domain.repository.StoreRepository) : SaleRepository {
    override fun observeAll() = dao.observeRecords().map { records -> records.map { record ->
        val payment = record.payments.firstOrNull()
        val snapshot = record.invoices.firstOrNull()?.let { kotlinx.serialization.json.Json.decodeFromString<Invoice>(it.payload) }
        record.sale.toDomain().copy(
            discount = snapshot?.discount, tax = snapshot?.tax ?: Tax(),
            items = record.items.map { SaleItem(it.productId, it.productName, it.quantity, it.unitPrice) },
            payment = Payment(Payment.PaymentType.valueOf(record.sale.paymentType), payment?.payment?.paidAmount ?: 0,
                payment?.installments?.sortedBy { it.monthNumber }?.map { Installment(it.monthNumber, it.percentage, it.amount, it.dueDate) } ?: emptyList()))
    } }
    override suspend fun create(sale: Sale, invoice: Invoice, requestId: String): Long = store.checkout(sale,invoice,requestId)
}

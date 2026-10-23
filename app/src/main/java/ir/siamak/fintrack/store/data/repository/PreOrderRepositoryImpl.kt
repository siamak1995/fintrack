package ir.siamak.fintrack.store.data.repository
import ir.siamak.fintrack.store.data.dao.PreOrderDao
import ir.siamak.fintrack.store.data.mapper.toDomain
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.repository.PreOrderRepository
import kotlinx.coroutines.flow.map

class PreOrderRepositoryImpl(private val dao: PreOrderDao, private val store: ir.siamak.fintrack.store.domain.repository.StoreRepository) : PreOrderRepository {
    override fun observeAll() = dao.observeRecords().map { records -> records.map { record -> record.order.toDomain().copy(
        items = record.items.map { PreOrderItem(it.rawMaterialId, it.materialName, it.quantity, it.unitPrice) }) } }
    override suspend fun create(order: PreOrder, invoice: Invoice, requestId: String): Long = store.placeOrder(order,invoice,requestId)
    override suspend fun updateStatus(id: Long, status: PreOrderStatus) = store.changeOrderStatus(id,status)
}

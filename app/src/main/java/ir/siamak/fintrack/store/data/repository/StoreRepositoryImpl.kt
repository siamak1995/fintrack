package ir.siamak.fintrack.store.data.repository

import androidx.room.withTransaction
import ir.siamak.fintrack.store.data.local.StoreDatabase
import ir.siamak.fintrack.store.data.entity.*
import ir.siamak.fintrack.store.data.mapper.*
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.model.Invoice.PaymentStatus
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoreRepositoryImpl(private val db: StoreDatabase) : StoreRepository {
    override fun observeStore() = db.storeDao().observe().map { it?.toDomain() }
    override suspend fun save(store: Store) = db.storeDao().upsert(store.toEntity())

    override suspend fun checkout(sale: Sale, invoice: Invoice, requestId: String): Long = db.withTransaction {
        require(requestId.isNotBlank() && invoice.requestId == requestId && invoice.invoiceNumber == "S-" + requestId)
        db.invoiceDao().byNumber(invoice.invoiceNumber)?.let { return@withTransaction it.id }
        require(sale.items.isNotEmpty() && sale.finalAmount >= 0)
        require(invoice.finalAmount == sale.finalAmount && invoice.items == sale.items)
        sale.items.forEach {
            require(it.quantity > 0 && it.unitPrice >= 0)
            check(db.productDao().consume(it.productId, it.quantity, it.unitPrice) == 1) { "قیمت یا موجودی کالا تغییر کرده است؛ دوباره بررسی کنید" }
        }
        val customerId = db.customerDao().insert(CustomerEntity(name = invoice.customer.name, phone = invoice.customer.phone, address = invoice.customer.address))
        val saleId = db.saleDao().insert(sale.copy(customerId = customerId).toEntity())
        db.saleDao().insertItems(sale.items.map { SaleItemEntity(saleId = saleId, productId = it.productId, productName = it.productName, quantity = it.quantity, unitPrice = it.unitPrice) })
        val paymentId = db.saleDao().insertPayment(PaymentEntity(saleId = saleId, preOrderId = null, type = sale.payment.type.name, paidAmount = sale.payment.paidAmount))
        db.saleDao().insertInstallments(sale.payment.installments.map { InstallmentEntity(paymentId = paymentId, monthNumber = it.monthNumber, percentage = it.percentage, amount = it.amount, dueDate = it.dueDate) })
        db.invoiceDao().upsert(invoice.entity(saleId = saleId, customerId = customerId))
    }

    override suspend fun placeOrder(order: PreOrder, invoice: Invoice, requestId: String): Long = db.withTransaction {
        require(requestId.isNotBlank() && invoice.requestId == requestId && invoice.invoiceNumber == "P-" + requestId)
        db.invoiceDao().byNumber(invoice.invoiceNumber)?.let { return@withTransaction it.id }
        require(order.agreedPrice > 0 && order.advancePayment in 0..order.agreedPrice)
        order.items.forEach {
            require(it.quantity.isFinite() && it.quantity > 0 && it.unitPrice >= 0)
            check(db.rawMaterialDao().consume(it.rawMaterialId, it.quantity, it.unitPrice) == 1) { "قیمت یا موجودی مواد اولیه تغییر کرده است" }
        }
        val customerId = db.customerDao().insert(CustomerEntity(name = invoice.customer.name, phone = invoice.customer.phone, address = invoice.customer.address))
        val id = db.preOrderDao().insert(order.copy(customerId = customerId).toEntity())
        db.preOrderDao().insertItems(order.items.map { PreOrderItemEntity(preOrderId = id, rawMaterialId = it.rawMaterialId, materialName = it.materialName, quantity = it.quantity, unitPrice = it.unitPrice) })
        db.saleDao().insertPayment(PaymentEntity(saleId = null, preOrderId = id, type = Payment.PaymentType.CASH.name, paidAmount = order.advancePayment))
        db.invoiceDao().upsert(invoice.copy(preOrderId = id).entity(orderId = id, customerId = customerId))
    }

    override suspend fun changeOrderStatus(orderId: Long, status: PreOrderStatus): Unit = db.withTransaction {
        val record = requireNotNull(db.preOrderDao().record(orderId)) { "سفارش یافت نشد" }
        val old = PreOrderStatus.valueOf(record.order.status)
        if (old == status) return@withTransaction

        ir.siamak.fintrack.store.domain.usecase.preorder.ValidatePreOrderTransitionUseCase()(record.order.toDomain(), status)
        if(status == PreOrderStatus.CANCELLED) {
            record.items.forEach { db.rawMaterialDao().restore(it.rawMaterialId,it.quantity) }
            // Preserve received funds. Cancellation is not a bank transfer or a claim that cash was refunded.
            db.invoiceDao().forOrder(orderId).forEach { entity ->
                val invoice = Json.decodeFromString<Invoice>(entity.payload)
                db.invoiceDao().upsert(entity.copy(payload = Json.encodeToString(invoice.copy(cancelled = true))))
            }
        }
        db.preOrderDao().status(orderId, status.name)
    }

    override suspend fun receivePayment(invoiceId: Long, amount: Long): Unit = db.withTransaction {
        require(amount > 0)
        val entity = requireNotNull(db.invoiceDao().get(invoiceId))
        val invoice = Json.decodeFromString<Invoice>(entity.payload).copy(id = entity.id)
        if (entity.preOrderId != null) require(db.preOrderDao().record(entity.preOrderId)?.order?.status != PreOrderStatus.CANCELLED.name) { "سفارش لغو شده است" }
        val updated = ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoicePaymentUseCase()(invoice, amount)
        val newPayment = requireNotNull(updated.payment)
        val paid = newPayment.paidAmount
        val payment = if (entity.saleId != null) db.saleDao().paymentForSale(entity.saleId) else db.saleDao().paymentForOrder(requireNotNull(entity.preOrderId))
        if (payment != null) db.saleDao().updatePaid(payment.id, paid)
        else db.saleDao().insertPayment(PaymentEntity(saleId = entity.saleId, preOrderId = entity.preOrderId, type = newPayment.type.name, paidAmount = paid))
        entity.preOrderId?.let { db.preOrderDao().updatePaid(it, paid) }
        db.invoiceDao().upsert(entity.copy(paymentStatus = updated.paymentStatus.name, payload = Json.encodeToString(updated)))
    }
    private fun Invoice.entity(saleId: Long? = null, orderId: Long? = null, customerId: Long): InvoiceEntity =
        InvoiceEntity(invoiceNumber = invoiceNumber, saleId = saleId, preOrderId = orderId, date = date, finalAmount = finalAmount,
            paymentStatus = paymentStatus.name, payload = Json.encodeToString(copy(customer = customer.copy(id = customerId))))
}

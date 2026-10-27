package ir.siamak.fintrack.store.data.repository

import androidx.room.withTransaction
import ir.siamak.fintrack.store.data.local.StoreDatabase
import ir.siamak.fintrack.store.data.entity.*
import ir.siamak.fintrack.store.data.mapper.*
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.model.Invoice.PaymentStatus
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoreRepositoryImpl(private val db: StoreDatabase, private val profiles: StoreProfilesDataSource? = null, private val ledger: StorePersonalLedger? = null) : StoreRepository {
    override fun observeStores() = combine(db.storeDao().observeAll(), observeProfiles()) { rows, settings -> rows.map { row ->
        val brand = settings.brands.firstOrNull { it.id == settings.storeBrands[row.id] }
        row.toDomain().let { if(brand == null) it else it.copy(brand = brand.name, brandImagePath = brand.image, brandId = brand.id) }
            .copy(startsOn = settings.periods[row.id]?.start, endsOn = settings.periods[row.id]?.end, settlement = settings.settlements[row.id])
    } }
    override fun observeProfiles() = profiles?.profiles ?: kotlinx.coroutines.flow.flowOf(StoreProfiles())
    override fun observeFinanceDirectory() = ledger?.directory ?: kotlinx.coroutines.flow.flowOf(StoreFinanceDirectory())
    override fun observeStore() = combine(observeStores(), observeProfiles()) { all, settings ->
        all.firstOrNull { it.id == settings.activeStoreId } ?: all.firstOrNull()
    }
    override suspend fun save(store: Store) {
        store.settlement?.let { requireNotNull(ledger).validate(it) }
        require((store.startsOn == null) == (store.endsOn == null)) { "تاریخ شروع و پایان را با هم وارد کنید" }
        if(store.startsOn != null) require(!java.time.LocalDate.parse(store.endsOn).isBefore(java.time.LocalDate.parse(store.startsOn))) { "پایان نمایشگاه نمی‌تواند قبل از شروع باشد" }
        val id = db.withTransaction {
            val id = store.id.takeIf { it > 0 } ?: db.storeDao().nextId()
            db.storeDao().upsert(store.copy(id = id).toEntity()); id
        }
        profiles?.update { it.copy(activeStoreId = if(store.id == 0L) id else it.activeStoreId,
            storeBrands = it.storeBrands - id + listOfNotNull(store.brandId?.let { brand -> id to brand }).toMap(),
            periods = it.periods - id + listOfNotNull(store.startsOn?.let { start -> id to ExhibitionPeriod(start,requireNotNull(store.endsOn)) }).toMap(),
            settlements = it.settlements - id + listOfNotNull(store.settlement?.let { account -> id to account }).toMap()) }
    }
    override suspend fun selectStore(id: Long) {
        require(observeStores().first().any { it.id == id }) { "فروشگاه یافت نشد" }
        requireNotNull(profiles).update { it.copy(activeStoreId = id) }
    }
    override suspend fun deleteStore(id: Long) {
        db.withTransaction {
            val first = db.storeDao().records().firstOrNull()?.id
            val used = db.invoiceDao().records().any {
                val owner = Json.decodeFromString<Invoice>(it.payload).store.id
                owner == id || (owner == 0L && first == id)
            }
            require(!used) { "فروشگاه دارای فاکتور قابل حذف نیست؛ سابقهٔ مالی باید حفظ شود" }
            db.storeDao().delete(id)
        }
        profiles?.update { it.copy(activeStoreId = if(it.activeStoreId == id) null else it.activeStoreId, storeBrands = it.storeBrands-id, periods = it.periods-id, settlements = it.settlements-id) }
    }
    override suspend fun saveBrand(brand: Brand) {
        require(brand.name.isNotBlank()) { "نام برند الزامی است" }
        requireNotNull(profiles).update { settings ->
            require(settings.brands.none { it.id != brand.id && it.name.equals(brand.name.trim(), true) }) { "برند تکراری است" }
            settings.copy(brands = settings.brands.filterNot { it.id == brand.id } + brand.copy(name = brand.name.trim()))
        }
    }
    override suspend fun deleteBrand(id: String) {
        // Historical store/invoice snapshots deliberately keep their name and image.
        requireNotNull(profiles).update {
            require(id !in it.storeBrands.values) { "این برند به فروشگاه متصل است؛ ابتدا برند فروشگاه را تغییر دهید" }
            it.copy(brands = it.brands.filterNot { brand -> brand.id == id })
        }
    }

    override suspend fun checkout(sale: Sale, invoice: Invoice, requestId: String): Long {
      val account = requireNotNull(invoice.settlement) { "حساب مقصد فروش انتخاب نشده است" }
      requireNotNull(ledger).validate(account)
      val id = db.withTransaction {
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
        db.invoiceDao().upsert(invoice.copy(displaySequence = nextSequence(false), settlement = account).entity(saleId = saleId, customerId = customerId))
      }
      postIfLinked(id)
      return id
    }

    override suspend fun placeOrder(order: PreOrder, invoice: Invoice, requestId: String): Long {
      invoice.store.settlement?.let { requireNotNull(ledger).validate(it) }
      val invoiceId = db.withTransaction {
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
        db.invoiceDao().upsert(invoice.copy(preOrderId = id, displaySequence = nextSequence(true), settlement = invoice.store.settlement).entity(orderId = id, customerId = customerId))
      }
      postIfLinked(invoiceId)
      return invoiceId
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

    override suspend fun receivePayment(invoiceId: Long, amount: Long) {
      db.withTransaction {
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
      postIfLinked(invoiceId)
    }
    private suspend fun nextSequence(order: Boolean): Long {
        val rows = db.invoiceDao().records().filter { (it.preOrderId != null) == order }
        return Math.addExact(maxOf(rows.size.toLong(), rows.maxOfOrNull { Json.decodeFromString<Invoice>(it.payload).displaySequence } ?: 0L), 1L)
    }
    override suspend fun syncInvoice(id: Long) {
        val snapshot = db.withTransaction {
            val entity = requireNotNull(db.invoiceDao().get(id))
            val invoice = Json.decodeFromString<Invoice>(entity.payload).copy(id = id, saleId = entity.saleId, preOrderId = entity.preOrderId)
            val account = invoice.settlement ?: profiles?.profiles?.first()?.settlements?.get(invoice.store.id)
            val linked = invoice.copy(settlement = requireNotNull(account) { "ابتدا کیف پول و شخص دریافت‌کننده را در فرم فروشگاه انتخاب کنید" })
            db.invoiceDao().upsert(entity.copy(payload = Json.encodeToString(linked)))
            linked
        }
        val posted = requireNotNull(ledger).post(snapshot)
        db.withTransaction {
            val entity = requireNotNull(db.invoiceDao().get(id))
            val current = Json.decodeFromString<Invoice>(entity.payload)
            db.invoiceDao().upsert(entity.copy(payload = Json.encodeToString(current.copy(walletPostedAmount = maxOf(current.walletPostedAmount,posted)))))
        }
    }
    private suspend fun postIfLinked(id: Long) {
        val invoice = db.invoiceDao().get(id)?.let { Json.decodeFromString<Invoice>(it.payload) } ?: return
        if(invoice.settlement == null) return
        try { syncInvoice(id) }
        catch(cancel: kotlinx.coroutines.CancellationException) { throw cancel }
        catch(_: Exception) { /* Durable invoice receipt remains pending; details exposes status + explicit retry. */ }
    }
    private fun Invoice.entity(saleId: Long? = null, orderId: Long? = null, customerId: Long): InvoiceEntity =
        InvoiceEntity(invoiceNumber = invoiceNumber, saleId = saleId, preOrderId = orderId, date = date, finalAmount = finalAmount,
            paymentStatus = paymentStatus.name, payload = Json.encodeToString(copy(customer = customer.copy(id = customerId))))
}

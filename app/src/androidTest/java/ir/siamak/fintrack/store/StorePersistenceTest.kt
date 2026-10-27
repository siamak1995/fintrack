package ir.siamak.fintrack.store
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ir.siamak.fintrack.store.data.local.*
import ir.siamak.fintrack.store.data.entity.*
import ir.siamak.fintrack.store.data.repository.*
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.usecase.sale.CheckoutSaleUseCase
import ir.siamak.fintrack.store.domain.usecase.preorder.PlacePreOrderUseCase
import ir.siamak.fintrack.store.core.pdf.InvoicePdfGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import kotlinx.serialization.encodeToString
import java.time.LocalDate
import java.io.ByteArrayOutputStream

@RunWith(AndroidJUnit4::class)
class StorePersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: StoreDatabase
    private lateinit var repository: StoreRepositoryImpl
    private lateinit var personal: ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
    private val workspace = StoreWorkspace(store = Store(1,"فروشگاه"),
        products = listOf(Product(1,"ماگ",stock = 5,price = 100_000),Product(2,"بشقاب",stock = 0,price = 200_000)),
        materials = listOf(RawMaterial(1,"خاک","کیلو",10_000,5.0)),
        finance = StoreFinanceDirectory(wallets = listOf(FinanceChoice(1,1,"نقد")),members = listOf(FinanceChoice(1,1,"خودم"))))
    @Before fun setup() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(context,StoreDatabase::class.java).build()
        personal = Room.inMemoryDatabaseBuilder(context,ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase::class.java).build()
        personal.walletDao().insertWallet(ir.siamak.fintrack.personalaccountant.data.local.entity.WalletEntity(id = 1,name = "نقد",balance = 0.0,color = "#008577",isCash = true))
        personal.memberDao().insertMember(ir.siamak.fintrack.personalaccountant.data.local.entity.MemberEntity(id = 1,name = "خودم",relation = "self",color = "#000000",icon = "person"))
        repository = StoreRepositoryImpl(db,ledger = StorePersonalLedger(personal))
        repository.save(requireNotNull(workspace.store))
        db.productDao().upsert(ProductEntity(1,"ماگ",null,null,null,5,100_000,null))
        db.productDao().upsert(ProductEntity(2,"بشقاب",null,null,null,0,200_000,null))
        db.rawMaterialDao().upsert(RawMaterialEntity(1,"خاک","کیلو",10_000,5.0,null,null))
    }
    @After fun close() { db.close(); personal.close() }
    private fun request(id: String = "test", count: Int = 1) = SaleRequest(Customer(name = "مشتری",phone = "0912"),null,mapOf(1L to count),requestId = id,settlement = StoreSettlementAccount(1,1,1))
    @Test fun salePersistsItemsPaymentScheduleAndSnapshotExactlyOnce() = runBlocking {
        val checkout = CheckoutSaleUseCase(repository)
        val request = request().copy(installmentMonths = 3,monthlyPercent = 5,firstDueDate = LocalDate.now().plusDays(1).toString())
        val id = checkout(request,workspace)
        assertEquals(id,checkout(request,workspace))
        assertEquals(4,db.productDao().get(1)!!.stock)
        val sale = SaleRepositoryImpl(db.saleDao(),repository).observeAll().first().single()
        assertEquals(1,sale.items.single().quantity)
        assertEquals(3,sale.payment.installments.size)
        assertEquals(115_000L,sale.finalAmount)
        val invoice = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single()
        assertEquals("مشتری",invoice.customer.name)
        repository.receivePayment(id,15_000)
        assertEquals(15_000L,InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single().payment!!.paidAmount)
        assertTrue(runCatching { repository.receivePayment(id,100_001) }.isFailure)
        repository.receivePayment(id,100_000)
        assertEquals(Invoice.PaymentStatus.SETTLED,InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single().paymentStatus)
    }
    @Test fun insufficientStockRollsBackAllPreviousWrites() = runBlocking {
        val stale = workspace.copy(products = workspace.products.map { it.copy(stock = 5) })
        val result = runCatching { CheckoutSaleUseCase(repository)(request().copy(quantities = linkedMapOf(1L to 1,2L to 1)),stale) }
        assertTrue(result.isFailure)
        assertEquals(5,db.productDao().get(1)!!.stock)
        assertTrue(db.saleDao().observeAll().first().isEmpty())
        assertTrue(db.invoiceDao().observeAll().first().isEmpty())
        db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM customers").use { it.moveToFirst(); assertEquals(0,it.getInt(0)) }
    }
    @Test fun concurrentCheckoutCannotOversell() = runBlocking {
        db.productDao().upsert(ProductEntity(1,"ماگ",null,null,null,1,100_000,null))
        val stale = workspace.copy(products = workspace.products.map { it.copy(stock = 1) })
        val results = (1..2).map { index -> async(Dispatchers.IO) { runCatching { CheckoutSaleUseCase(repository)(request("race-" + index),stale) } } }.awaitAll()
        assertEquals(1,results.count { it.isSuccess })
        assertEquals(0,db.productDao().get(1)!!.stock)
        assertEquals(1,db.saleDao().observeAll().first().size)
    }
    @Test fun invoiceSnapshotSurvivesCatalogChangesAndDeletion() = runBlocking {
        CheckoutSaleUseCase(repository)(request(),workspace)
        repository.save(Store(1,"نام جدید"))
        db.productDao().delete(1)
        val invoice = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single()
        assertEquals("فروشگاه",invoice.store.name)
        assertEquals("ماگ",invoice.items.single().productName)
        assertEquals(100_000L,invoice.items.single().unitPrice)
    }
    private fun order(advance: Long = 0) = PreOrderRequest(Customer(name = "مشتری",phone = "0912"),"سرامیک آبی",
        LocalDate.now().plusDays(20).toString(),mapOf(1L to 2.0),100_000,50_000,10_000,300_000,advance,false,"order")
    @Test fun preorderReservesMaterialsCancellationRestoresOnlyOnce() = runBlocking {
        val place = PlacePreOrderUseCase(repository)
        val invoiceId = place(order(),workspace)
        assertEquals(invoiceId,place(order(),workspace))
        assertEquals(3.0,db.rawMaterialDao().get(1)!!.quantity,0.000001)
        val orderId = PreOrderRepositoryImpl(db.preOrderDao(),repository).observeAll().first().single().id
        repository.changeOrderStatus(orderId,PreOrderStatus.CANCELLED)
        repository.changeOrderStatus(orderId,PreOrderStatus.CANCELLED)
        assertEquals(5.0,db.rawMaterialDao().get(1)!!.quantity,0.000001)
        assertTrue(runCatching { repository.receivePayment(invoiceId,1000) }.isFailure)
    }

    @Test fun cancelledPaidOrderPreservesReceiptAndBlocksFurtherCollection() = runBlocking {
        val invoiceId = PlacePreOrderUseCase(repository)(order(90_000),workspace)
        val orderId = PreOrderRepositoryImpl(db.preOrderDao(),repository).observeAll().first().single().id
        assertTrue(runCatching { MaterialRepositoryImpl(db.rawMaterialDao()).delete(1) }.isFailure)
        repository.changeOrderStatus(orderId,PreOrderStatus.CANCELLED)
        val invoice = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single()
        assertTrue(invoice.cancelled)
        assertEquals(90_000L,invoice.payment!!.paidAmount)
        assertTrue(runCatching { repository.receivePayment(invoiceId,1) }.isFailure)
        assertEquals(5.0,db.rawMaterialDao().get(1)!!.quantity,0.0)
    }
    @Test fun preorderTracksAdvanceBalanceAndDelivery() = runBlocking {
        val invoiceId = PlacePreOrderUseCase(repository)(order(90_000),workspace)
        val orderId = PreOrderRepositoryImpl(db.preOrderDao(),repository).observeAll().first().single().id
        assertTrue(runCatching { repository.changeOrderStatus(orderId,PreOrderStatus.DELIVERED) }.isFailure)
        repository.changeOrderStatus(orderId,PreOrderStatus.IN_PROGRESS)
        repository.changeOrderStatus(orderId,PreOrderStatus.READY)
        assertTrue(runCatching { repository.changeOrderStatus(orderId,PreOrderStatus.NEW) }.isFailure)
        repository.changeOrderStatus(orderId,PreOrderStatus.DELIVERED)
        repository.receivePayment(invoiceId,210_000)
        repository.changeOrderStatus(orderId,PreOrderStatus.DELIVERED)
        val order = PreOrderRepositoryImpl(db.preOrderDao(),repository).observeAll().first().single()
        assertEquals(PreOrderStatus.DELIVERED,order.status)
        assertEquals(300_000L,order.advancePayment)
        assertEquals(2.0,order.items.single().quantity,0.0)
    }


    @Test fun restoredCommittedDraftResolvesExistingInvoiceEvenWhenStockIsNowZero(): Unit = runBlocking {
        val request = request("restore",5)
        val id = CheckoutSaleUseCase(repository)(request,workspace)
        val draft = ir.siamak.fintrack.store.presentation.sales.create.SaleDraft(name = "مشتری",phone = "0912",
            quantities = mapOf(1L to 5),step = 4,requestId = "restore")
        val observe = ir.siamak.fintrack.store.domain.usecase.store.ObserveStoreWorkspaceUseCase(repository,
            SellerRepositoryImpl(db.sellerDao()),ProductRepositoryImpl(db.productDao()),MaterialRepositoryImpl(db.rawMaterialDao()),
            SaleRepositoryImpl(db.saleDao(),repository),PreOrderRepositoryImpl(db.preOrderDao(),repository),InvoiceRepositoryImpl(db.invoiceDao()))
        val owner = androidx.lifecycle.ViewModelStore()
        lateinit var viewModel: ir.siamak.fintrack.store.presentation.sales.create.CreateSaleViewModel
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModel = ir.siamak.fintrack.store.presentation.sales.create.CreateSaleViewModel(
                androidx.lifecycle.SavedStateHandle(mapOf("draft" to kotlinx.serialization.json.Json.encodeToString(draft))),
                observe,ir.siamak.fintrack.store.domain.usecase.sale.CreateSaleUseCase(CheckoutSaleUseCase(repository)),ProductRepositoryImpl(db.productDao()))
            owner.put("draft",viewModel)
        }
        try {
            val restored = withTimeout(10_000) { viewModel.state.first { it.invoiceId != null } }
            assertEquals(id,restored.invoiceId)
            assertEquals(0,db.productDao().get(1)!!.stock)
            assertEquals(1,db.saleDao().observeAll().first().size)
        } finally { InstrumentationRegistry.getInstrumentation().runOnMainSync { owner.clear() } }
    }
    @Test fun storedInvoiceAndInventorySurviveDatabaseReopen() = runBlocking {
        val name = "store-restart-test"
        context.deleteDatabase(name)
        try {
            Room.databaseBuilder(context,StoreDatabase::class.java,name).build().useDatabase { disk ->
                val store = StoreRepositoryImpl(disk,ledger = StorePersonalLedger(personal))
                store.save(Store(1,"فروشگاه پایدار"))
                disk.productDao().upsert(ProductEntity(1,"ماگ",null,null,null,5,100_000,null))
                CheckoutSaleUseCase(store)(request("disk"),workspace)
            }
            Room.databaseBuilder(context,StoreDatabase::class.java,name).build().useDatabase { disk ->
                assertEquals(4,disk.productDao().get(1)!!.stock)
                val invoice = InvoiceRepositoryImpl(disk.invoiceDao()).observeAll().first().single()
                assertEquals("disk",invoice.requestId)
                assertEquals("مشتری",invoice.customer.name)
            }
        } finally { context.deleteDatabase(name) }
    }
    @Test fun pdfIsValidAndSupportsMultiplePages() = runBlocking {
        CheckoutSaleUseCase(repository)(request(),workspace)
        val invoice = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single()
        val bytes = ByteArrayOutputStream().use { stream ->
            InvoicePdfGenerator().generate(invoice.copy(items = List(120) { SaleItem(1,"کالای فارسی آزمایشی " + it,1,100_000) }),stream)
            stream.toByteArray()
        }
        assertEquals("%PDF",bytes.take(4).toByteArray().decodeToString())
        val file = java.io.File(context.cacheDir,"store-pdf-test.pdf").apply { writeBytes(bytes) }
        android.os.ParcelFileDescriptor.open(file,android.os.ParcelFileDescriptor.MODE_READ_ONLY).use {
            android.graphics.pdf.PdfRenderer(it).use { renderer -> assertTrue(renderer.pageCount > 1); renderer.openPage(0).use { page ->
                val bitmap = android.graphics.Bitmap.createBitmap(page.width * 2,page.height * 2,android.graphics.Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(android.graphics.Color.WHITE)
                page.render(bitmap,null,null,android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                java.io.File(context.cacheDir,"store-invoice-preview.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
                bitmap.recycle()
            } }
        }
        assertTrue(file.delete())
    }
    @Test fun saleCreditsChosenWalletOnlyOnce() = runBlocking {
        val usecase = CheckoutSaleUseCase(repository)
        val id = usecase(request(),workspace)
        assertEquals(id,usecase(request(),workspace))
        assertEquals(100_000.0,personal.walletDao().getWalletById(1)!!.balance,0.0)
        assertEquals(1,personal.transactionDao().getAllTransactionsSync().size)
        assertEquals(100_000L,InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single().walletPostedAmount)
    }
    @Test fun duplicateProductNamesAreRejectedAndColorPersists() = runBlocking {
        val products = ProductRepositoryImpl(db.productDao())
        assertTrue(runCatching { products.save(Product(name = " ماگ ")) }.isFailure)
        products.save(Product(name = "کالای جدید", stock = 2, price = 120000, color = "سبز"))
        assertEquals("سبز",products.observeAll().first().first { it.name == "کالای جدید" }.color)
    }
}


private inline fun <T : androidx.room.RoomDatabase, R> T.useDatabase(block: (T) -> R): R = try { block(this) } finally { close() }

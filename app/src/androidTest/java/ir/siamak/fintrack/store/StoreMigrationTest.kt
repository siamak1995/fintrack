package ir.siamak.fintrack.store
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ir.siamak.fintrack.store.data.local.*
import ir.siamak.fintrack.store.data.repository.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoreMigrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    @get:Rule val source = MigrationTestHelper(instrumentation, "ir.siamak.fintrack.storeaccountant.data.local.StoreDatabase")
    @get:Rule val target = MigrationTestHelper(instrumentation, StoreDatabase::class.java.canonicalName)
    @Test fun productColorMigrationPreservesCatalog() {
        val name = "store-color-migration"
        target.createDatabase(name,2).use { db -> db.execSQL("INSERT INTO products(id,name,material,size,weight,stock,price,image) VALUES(1,'ماگ',NULL,NULL,NULL,20,150000,NULL)") }
        target.runMigrationsAndValidate(name,3,true,StoreMigrations.FROM_2_TO_3).use { db ->
            db.query("SELECT name,stock,price,color FROM products WHERE id=1").use { cursor ->
                assertTrue(cursor.moveToFirst()); assertEquals("ماگ",cursor.getString(0)); assertEquals(20,cursor.getInt(1)); assertEquals(150000L,cursor.getLong(2)); assertTrue(cursor.isNull(3))
            }
        }
        context.deleteDatabase(name)
    }
    @Test fun activeLegacyDatabasePreservesSalesWalletReferenceCatalogAndCustomers(): Unit = runBlocking {
        val name = "store-legacy-migration-test"
        source.createDatabase(name,1).use { db ->
            db.execSQL("INSERT INTO stores(id,name,brand) VALUES(1,'فروشگاه قدیمی','برند')")
            db.execSQL("INSERT INTO products(id,name,stock,price) VALUES(1,'ماگ',3,100000)")
            db.execSQL("INSERT INTO raw_materials(id,name,unit,pricePerUnit,quantity) VALUES(1,'خاک','کیلو',1000,5.5)")
            db.execSQL("INSERT INTO customers(id,name,phone) VALUES(1,'مشتری','0912')")
            db.execSQL("INSERT INTO sales(id,customerId,date,totalAmount,discountValue,discountType,taxPercent,finalAmount,walletId) VALUES(1,1,'2026-10-23',200000,10,'PERCENT',0,180000,77)")
            db.execSQL("INSERT INTO sale_items(id,saleId,productId,quantity,unitPrice) VALUES(1,1,1,2,100000)")
        }
        target.runMigrationsAndValidate(name,2,false,StoreMigrations.FROM_1_TO_2).use { db ->
            db.query("SELECT walletId FROM legacy_sales_v1 WHERE id=1").use { it.moveToFirst(); assertEquals(77,it.getInt(0)) }
            db.query("SELECT subtotal,discountAmount,finalAmount FROM sales WHERE id=1").use { it.moveToFirst(); assertEquals(200000,it.getInt(0)); assertEquals(20000,it.getInt(1)); assertEquals(180000,it.getInt(2)) }
        }
        Room.databaseBuilder(context,StoreDatabase::class.java,name).addMigrations(StoreMigrations.FROM_1_TO_2).build().useDatabase { db ->
            val invoice = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single()
            assertEquals("مشتری",invoice.customer.name)
            assertEquals(180000L,invoice.finalAmount)
            assertEquals("ماگ",invoice.items.single().productName)
            assertEquals(3,db.productDao().get(1)!!.stock)
            assertEquals(5.5,db.rawMaterialDao().get(1)!!.quantity,0.0)
        }
        context.deleteDatabase(name)
    }

    @Test fun scaffoldPaymentAndInstallmentRecordsSurviveMigration(): Unit = runBlocking {
        val name = "store-scaffold-paid-test"
        target.createDatabase(name,1).use { db ->
            db.execSQL("INSERT INTO stores(id,name,brand) VALUES(1,'فروشگاه','')")
            db.execSQL("INSERT INTO customers(id,name,phone) VALUES(1,'مشتری','0912')")
            db.execSQL("INSERT INTO sales(id,customerId,sellerId,subtotal,discountAmount,taxAmount,finalAmount,paymentType,createdAt) VALUES(1,1,NULL,100000,0,0,115000,'INSTALLMENT','2026-10-23')")
            db.execSQL("INSERT INTO payments(id,saleId,preOrderId,type,paidAmount) VALUES(1,1,NULL,'INSTALLMENT',15000)")
            db.execSQL("INSERT INTO installments(id,paymentId,monthNumber,percentage,amount,dueDate) VALUES(1,1,1,5,115000,'2026-11-23')")
            db.execSQL("INSERT INTO invoices(id,invoiceNumber,saleId,preOrderId,date,finalAmount,paymentStatus) VALUES(1,'OLD-1',1,NULL,'2026-10-23',115000,'INSTALLMENT')")
        }
        target.runMigrationsAndValidate(name,3,true,StoreMigrations.FROM_1_TO_2,StoreMigrations.FROM_2_TO_3).close()
        Room.databaseBuilder(context,StoreDatabase::class.java,name).build().useDatabase { db ->
            val invoice = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first().single()
            assertEquals("OLD-1",invoice.invoiceNumber)
            assertEquals(15000L,invoice.payment!!.paidAmount)
            assertEquals(1,invoice.payment.installments.size)
            assertEquals(115000L,invoice.payment.installments.single().amount)
        }
        context.deleteDatabase(name)
    }
    @Test fun legacyPreorderRetainsMaterialsReceiptsCancellationAndConflictingInvoiceNumber(): Unit = runBlocking {
        val name = "store-scaffold-preorder-test"
        target.createDatabase(name,1).use { db ->
            db.execSQL("INSERT INTO stores(id,name,brand) VALUES(1,'فروشگاه','')")
            db.execSQL("INSERT INTO customers(id,name,phone) VALUES(1,'مشتری','0912')")
            db.execSQL("INSERT INTO raw_materials(id,name,unit,pricePerUnit,quantity) VALUES(1,'خاک','کیلو',10000,3.5)")
            db.execSQL("INSERT INTO pre_orders(id,customerId,description,deliveryDate,agreedPrice,shippingCost,status,advancePayment) VALUES(1,1,'سفارش قدیمی','2026-11-23',100000,0,'CANCELLED',20000)")
            db.execSQL("INSERT INTO pre_order_items(id,preOrderId,rawMaterialId,materialName,quantity,unitPrice) VALUES(1,1,1,'خاک',1.5,10000)")
            db.execSQL("INSERT INTO payments(id,saleId,preOrderId,type,paidAmount) VALUES(1,NULL,1,'CASH',30000)")
            db.execSQL("INSERT INTO invoices(id,invoiceNumber,saleId,preOrderId,date,finalAmount,paymentStatus) VALUES(1,'LEGACY-P-1',NULL,NULL,'2026-10-23',1000,'UNSETTLED')")
        }
        target.runMigrationsAndValidate(name,3,true,StoreMigrations.FROM_1_TO_2,StoreMigrations.FROM_2_TO_3).close()
        Room.databaseBuilder(context,StoreDatabase::class.java,name).build().useDatabase { db ->
            val invoices = InvoiceRepositoryImpl(db.invoiceDao()).observeAll().first()
            assertEquals(2,invoices.size)
            assertEquals(2,invoices.map { it.invoiceNumber }.toSet().size)
            val orderInvoice = invoices.single { it.preOrderId == 1L }
            assertTrue(orderInvoice.cancelled)
            assertEquals(30000L,orderInvoice.payment!!.paidAmount)
            assertEquals("خاک",orderInvoice.materialItems.single().materialName)
            assertEquals(1.5,orderInvoice.materialItems.single().quantity,0.0)
            assertEquals(3.5,db.rawMaterialDao().get(1)!!.quantity,0.0)
        }
        context.deleteDatabase(name)
    }
    @Test fun emptyScaffoldV1MigratesAndReopens() {
        val name = "store-scaffold-migration-test"
        target.createDatabase(name,1).close()
        target.runMigrationsAndValidate(name,3,true,StoreMigrations.FROM_1_TO_2,StoreMigrations.FROM_2_TO_3).close()
        Room.databaseBuilder(context,StoreDatabase::class.java,name).addMigrations(StoreMigrations.FROM_1_TO_2).build().useDatabase { db ->
            assertEquals(3,db.openHelper.writableDatabase.version)
        }
        context.deleteDatabase(name)
    }
}

private inline fun <T : androidx.room.RoomDatabase, R> T.useDatabase(block: (T) -> R): R = try { block(this) } finally { close() }

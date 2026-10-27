package ir.siamak.fintrack.store.data.local
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.database.Cursor
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.model.Invoice.PaymentStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Retains the original v1 sales tables as audit copies, including legacy wallet references. */
object StoreMigrations {
    val FROM_2_TO_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) { db.execSQL("ALTER TABLE products ADD COLUMN color TEXT") }
    }

    val FROM_1_TO_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val legacy = db.query("PRAGMA table_info(sales)").use { cursor ->
                var found = false
                while (cursor.moveToNext()) if (cursor.getString(1) == "date") found = true
                found
            }
            if (legacy) {
                db.execSQL("ALTER TABLE sales RENAME TO legacy_sales_v1")
                db.execSQL("ALTER TABLE sale_items RENAME TO legacy_sale_items_v1")
            }
                db.execSQL("CREATE TABLE IF NOT EXISTS `stores` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `brand` TEXT NOT NULL, `logoPath` TEXT, `phone` TEXT, `address` TEXT, `description` TEXT, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `sellers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firstName` TEXT NOT NULL, `lastName` TEXT NOT NULL, `phone` TEXT NOT NULL, `address` TEXT, `description` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `customers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `address` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `material` TEXT, `size` TEXT, `weight` TEXT, `stock` INTEGER NOT NULL, `price` INTEGER NOT NULL, `image` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `raw_materials` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `unit` TEXT NOT NULL, `pricePerUnit` INTEGER NOT NULL, `quantity` REAL NOT NULL, `purchaseDate` TEXT, `shippingCost` INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `sales` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `sellerId` INTEGER, `subtotal` INTEGER NOT NULL, `discountAmount` INTEGER NOT NULL, `taxAmount` INTEGER NOT NULL, `finalAmount` INTEGER NOT NULL, `paymentType` TEXT NOT NULL, `createdAt` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `sale_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `saleId` INTEGER NOT NULL, `productId` INTEGER NOT NULL, `productName` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `unitPrice` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `saleId` INTEGER, `preOrderId` INTEGER, `type` TEXT NOT NULL, `paidAmount` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `installments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `paymentId` INTEGER NOT NULL, `monthNumber` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `amount` INTEGER NOT NULL, `dueDate` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `invoices` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `invoiceNumber` TEXT NOT NULL, `saleId` INTEGER, `preOrderId` INTEGER, `date` TEXT NOT NULL, `finalAmount` INTEGER NOT NULL, `paymentStatus` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `pre_orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `description` TEXT NOT NULL, `deliveryDate` TEXT NOT NULL, `agreedPrice` INTEGER NOT NULL, `shippingCost` INTEGER NOT NULL, `status` TEXT NOT NULL, `advancePayment` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `pre_order_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `preOrderId` INTEGER NOT NULL, `rawMaterialId` INTEGER NOT NULL, `materialName` TEXT NOT NULL, `quantity` REAL NOT NULL, `unitPrice` INTEGER NOT NULL)")
            db.execSQL("ALTER TABLE invoices ADD COLUMN payload TEXT NOT NULL DEFAULT 'NONE'")
            if (legacy) {
                db.execSQL("INSERT INTO sales(id,customerId,sellerId,subtotal,discountAmount,taxAmount,finalAmount,paymentType,createdAt) SELECT id,customerId,NULL,totalAmount,CASE WHEN discountType='PERCENT' THEN totalAmount * discountValue / 100 ELSE discountValue END,MAX(0,finalAmount-totalAmount+CASE WHEN discountType='PERCENT' THEN totalAmount*discountValue/100 ELSE discountValue END),finalAmount,'CASH',date FROM legacy_sales_v1")
                db.execSQL("INSERT INTO sale_items(id,saleId,productId,productName,quantity,unitPrice) SELECT i.id,i.saleId,i.productId,COALESCE(p.name,'کالای قدیمی'),i.quantity,i.unitPrice FROM legacy_sale_items_v1 i LEFT JOIN products p ON p.id=i.productId")
            }
            // Duplicate old invoice numbers are preserved but made unambiguous before the unique index.

            val numbers = mutableSetOf<String>()
            db.query("SELECT invoiceNumber FROM invoices").use { c -> while(c.moveToNext()) numbers.add(c.getString(0)) }
            val seen = mutableSetOf<String>()
            db.query("SELECT id,invoiceNumber FROM invoices ORDER BY id").use { c -> while(c.moveToNext()) {
                val number = c.getString(1)
                if(!seen.add(number)) {
                    var candidate = number + "-legacy-" + c.getLong(0)
                    while(candidate in numbers) candidate += "-"
                    numbers.add(candidate)
                    db.execSQL("UPDATE invoices SET invoiceNumber=? WHERE id=?", arrayOf(candidate,c.getLong(0)))
                }
            } }

            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_invoices_invoiceNumber ON invoices(invoiceNumber)")
            backfillSnapshots(db)
        }
    }

    private fun Cursor.text(name: String): String = getString(getColumnIndexOrThrow(name)).orEmpty()
    private fun Cursor.long(name: String): Long = getLong(getColumnIndexOrThrow(name))
    private fun customer(db: SupportSQLiteDatabase, id: Long): Customer =
        db.query("SELECT * FROM customers WHERE id=?", arrayOf(id)).use { c ->
            if (c.moveToFirst()) Customer(id, c.text("name"), c.text("phone"), c.text("address"))
            else Customer(id, "مشتری قدیمی", "", null)
        }
    private fun backfillSnapshots(db: SupportSQLiteDatabase) {
        val store = db.query("SELECT * FROM stores ORDER BY id LIMIT 1").use { c ->
            if (c.moveToFirst()) Store(c.long("id"), c.text("name"), c.text("brand"), c.text("logoPath"), c.text("phone"), c.text("address"), c.text("description"))
            else Store(name = "فروشگاه")
        }
        db.query("SELECT * FROM sales").use { c -> while (c.moveToNext()) {
            val saleId = c.long("id")
            val items = db.query("SELECT * FROM sale_items WHERE saleId=?", arrayOf(saleId)).use { i ->
                buildList { while (i.moveToNext()) add(SaleItem(i.long("productId"), i.text("productName"), i.long("quantity").toInt(), i.long("unitPrice"))) }
            }

            val payment = db.query("SELECT * FROM payments WHERE saleId=? ORDER BY id LIMIT 1",arrayOf(saleId)).use { p ->
                if(p.moveToFirst()) {
                    val schedule = db.query("SELECT * FROM installments WHERE paymentId=? ORDER BY monthNumber",arrayOf(p.long("id"))).use { i ->
                        buildList { while(i.moveToNext()) add(Installment(i.long("monthNumber").toInt(),i.long("percentage").toInt(),i.long("amount"),i.text("dueDate"))) }
                    }
                    Payment(Payment.PaymentType.valueOf(p.text("type")),p.long("paidAmount"),schedule)
                } else Payment(Payment.PaymentType.valueOf(c.text("paymentType")),0)
            }
            val invoice = Invoice(invoiceNumber = "LEGACY-S-" + saleId, date = c.text("createdAt"), store = store,
                customer = customer(db, c.long("customerId")), items = items, discountAmount = c.long("discountAmount"),
                taxAmount = c.long("taxAmount"), finalAmount = c.long("finalAmount"), payment = payment,
                description = "انتقال از نسخه قدیمی؛ برای فروش فاقد سابقه پرداخت، مبلغ دریافتی نامشخص است", paymentStatus = if(payment.paidAmount >= c.long("finalAmount")) PaymentStatus.SETTLED else if(payment.type == Payment.PaymentType.INSTALLMENT) PaymentStatus.INSTALLMENT else PaymentStatus.UNSETTLED)
            upsertLegacy(db, invoice, saleId, null)
        } }
        db.query("SELECT * FROM pre_orders").use { c -> while (c.moveToNext()) {
            val id = c.long("id")
            val items = db.query("SELECT * FROM pre_order_items WHERE preOrderId=?", arrayOf(id)).use { i ->
                buildList { while(i.moveToNext()) add(PreOrderItem(i.long("rawMaterialId"), i.text("materialName"), i.getDouble(i.getColumnIndexOrThrow("quantity")), i.long("unitPrice"))) }
            }
            val paid = db.query("SELECT paidAmount FROM payments WHERE preOrderId=? ORDER BY id LIMIT 1", arrayOf(id)).use { p ->
                if(p.moveToFirst()) p.getLong(0) else c.long("advancePayment")
            }
            val invoice = Invoice(invoiceNumber = "LEGACY-P-" + id, date = c.text("deliveryDate"), store = store,
                customer = customer(db, c.long("customerId")), description = c.text("description"), deliveryDate = c.text("deliveryDate"),
                preOrderId = id, materialItems = items, cancelled = c.text("status") == PreOrderStatus.CANCELLED.name,
                finalAmount = c.long("agreedPrice"), payment = Payment(Payment.PaymentType.CASH, paid),
                paymentStatus = if(paid >= c.long("agreedPrice")) PaymentStatus.SETTLED else PaymentStatus.UNSETTLED)
            upsertLegacy(db, invoice, null, id)

        } }
        db.query("SELECT * FROM invoices WHERE payload='NONE'").use { c -> while(c.moveToNext()) {
            val snapshot = Invoice(invoiceNumber = c.text("invoiceNumber"), date = c.text("date"), store = store,
                customer = Customer(name = "مشتری قدیمی",phone = ""),finalAmount = c.long("finalAmount"),
                description = "فاکتور قدیمی بدون ارتباط؛ اطلاعات اصلی حفظ شده است",paymentStatus = PaymentStatus.UNSETTLED)
            db.execSQL("UPDATE invoices SET payload=? WHERE id=?",arrayOf(Json.encodeToString(snapshot),c.long("id")))
        } }
    }
    private fun upsertLegacy(db: SupportSQLiteDatabase, invoice: Invoice, saleId: Long?, orderId: Long?) {
        val existing = db.query("SELECT id,invoiceNumber FROM invoices WHERE saleId IS ? AND preOrderId IS ?", arrayOf(saleId, orderId)).use { c -> if(c.moveToFirst()) c.getLong(0) to c.getString(1) else null }
        if(existing == null) {
            var number = invoice.invoiceNumber
            while(db.query("SELECT 1 FROM invoices WHERE invoiceNumber=?", arrayOf(number)).use { it.moveToFirst() }) number += "-legacy"
            db.execSQL("INSERT INTO invoices(invoiceNumber,saleId,preOrderId,date,finalAmount,paymentStatus,payload) VALUES(?,?,?,?,?,?,?)",
                arrayOf(number, saleId, orderId, invoice.date, invoice.finalAmount, invoice.paymentStatus.name, Json.encodeToString(invoice.copy(invoiceNumber = number))))
        }
        else db.execSQL("UPDATE invoices SET payload=? WHERE id=? AND payload='NONE'", arrayOf(Json.encodeToString(invoice.copy(invoiceNumber = existing.second)), existing.first))
    }
}

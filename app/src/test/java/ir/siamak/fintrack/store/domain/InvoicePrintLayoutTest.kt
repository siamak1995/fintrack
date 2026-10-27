package ir.siamak.fintrack.store.domain

import ir.siamak.fintrack.store.core.pdf.InvoicePrintLayout
import ir.siamak.fintrack.store.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class InvoicePrintLayoutTest {
    @Test fun multipageInvoiceReservesSignatureSpaceAndKeepsTrackingBelowDate() {
        val invoice = Invoice(id = 1, invoiceNumber = "internal", displaySequence = 12, date = "2026-10-26",
            store = Store(name = "فروشگاه نمونه", brand = "برند"),
            customer = Customer(name = "مشتری نمونه", phone = "09123456789"),
            seller = Seller(id = 1, firstName = "فروشنده", lastName = "نمونه", phone = "09123456789", signaturePath = "content://signature"),
            items = (1L..60L).map { SaleItem(it, "کالای نمونه با نام فارسی طولانی برای بررسی صفحه‌بندی", 1, 100000) },
            finalAmount = 6000000, paymentStatus = Invoice.PaymentStatus.UNSETTLED)
        val document = InvoicePrintLayout().create(invoice)
        assertTrue(document.pages.size > 1)
        val signature = document.pages.flatten().single { it.kind == "signature" }
        assertEquals(67f, signature.height, .01f)
        assertTrue(signature.y + signature.height <= 565f)
        val firstPage = document.pages.first()
        assertTrue(firstPage.any { it.text.startsWith("نام مشتری:") })
        assertTrue(firstPage.single { it.text.startsWith("کد رهگیری:") }.y > firstPage.single { it.text.startsWith("تاریخ فاکتور:") }.y)
        assertFalse(document.pages.flatten().any { it.text.contains("internal") })
    }
    @Test fun threeShortCashItemsAndSignatureFitOnOnePage() {
        val invoice = Invoice(id = 1, invoiceNumber = "internal", date = "2026-10-26",
            store = Store(name = "فروشگاه"), customer = Customer(name = "مشتری", phone = "09123456789"),
            items = (1L..3L).map { SaleItem(it, "کالا", 1, 100000) }, finalAmount = 300000,
            payment = Payment(Payment.PaymentType.CASH, 300000), paymentStatus = Invoice.PaymentStatus.SETTLED)
        val document = InvoicePrintLayout().create(invoice)
        assertEquals(1, document.pages.size)
        assertEquals(67f, document.pages.flatten().single { it.kind == "signature" }.height, .01f)
    }
    @Test fun brandIsPlacedDirectlyBelowThanksAndStorePhotoIsNotPrinted() {
        val base = Invoice(invoiceNumber = "internal",date = "2026-10-26",store = Store(name = "فروشگاه",logoPath = "store-photo",brandImagePath = "brand-image"),
            customer = Customer(name = "مشتری",phone = "0912"),finalAmount = 100,paymentStatus = Invoice.PaymentStatus.SETTLED)
        val elements = InvoicePrintLayout().create(base).pages.flatten()
        val brand = elements.single { it.kind == "logo" }; val thanks = elements.single { it.text == "با نهایت تشکر" }
        assertEquals(thanks.x,brand.x,.01f); assertEquals(thanks.width,brand.width,.01f); assertTrue(brand.y > thanks.y)
        assertTrue(brand.y + brand.height <= 565f)
        assertFalse(InvoicePrintLayout().create(base.copy(store = base.store.copy(brandImagePath = null))).pages.flatten().any { it.kind == "logo" })
    }
}

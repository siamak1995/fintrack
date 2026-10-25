package ir.siamak.fintrack.store

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import ir.siamak.fintrack.store.core.pdf.*
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.usecase.store.CalculateStoreReportUseCase
import java.io.File
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class StoreReportPdfTest {
    @Test fun reportExportsAllPagesAndPersianInvoiceStillRenders() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val folder = File(context.cacheDir,"sol-light-pdf-qa").apply { mkdirs() }
        val invoices = (1L..45L).map { id -> Invoice(id=id,invoiceNumber="S-$id",date="2026-10-24",store=Store(name="فروشگاه آزمایشی",brand="تست مستقل"),
            customer=Customer(id=id,name="مشتری آزمایشی $id",phone="09120000000"),
            items=listOf(SaleItem(1,"ظرف سرامیکی آبی با توضیح فارسی طولانی برای بررسی شکست متن",3,150000)),
            finalAmount=450000,payment=Payment(Payment.PaymentType.CASH,450000),paymentStatus=Invoice.PaymentStatus.SETTLED) }
        val report = CalculateStoreReportUseCase()(invoices,StoreReportFilter(from=LocalDate.parse("2026-10-01"),to=LocalDate.parse("2026-10-24")))
        val reportPdf = File(folder,"store-report.pdf"); reportPdf.outputStream().use { StoreReportPdfGenerator().generate(report,it) }
        val invoicePdf = File(folder,"sale-invoice.pdf"); invoicePdf.outputStream().use { InvoicePdfGenerator().generate(invoices[0],it) }
        listOf(reportPdf,invoicePdf).forEach { file ->
            ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor -> PdfRenderer(descriptor).use { renderer ->
                assertTrue(renderer.pageCount >= 1)
                if(file == reportPdf) assertTrue(renderer.pageCount > 1)
                repeat(renderer.pageCount) { index -> renderer.openPage(index).use { page ->
                    val bitmap = Bitmap.createBitmap(page.width*2,page.height*2,Bitmap.Config.ARGB_8888)
                    bitmap.eraseColor(android.graphics.Color.WHITE); page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    File(folder,file.nameWithoutExtension + "-" + (index+1) + ".png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }
                    bitmap.recycle()
                } }
            } }
        }
    }
}

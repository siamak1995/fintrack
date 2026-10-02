package ir.siamak.fintrack.store.core.pdf

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import ir.siamak.fintrack.store.domain.model.Invoice
import java.io.OutputStream

/** Generates a portable one-page invoice PDF using Android's platform PDF API. */
class InvoicePdfGenerator {
    fun generate(invoice: Invoice, output: OutputStream) {
        val document = PdfDocument()
        try {
            val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, PAGE_NUMBER).create())
            val paint = Paint().apply { textSize = TEXT_SIZE }
            var y = TOP_MARGIN
            fun line(value: String) { page.canvas.drawText(value, LEFT_MARGIN, y, paint); y += LINE_HEIGHT }
            line("Invoice: ${invoice.invoiceNumber}")
            line("Date: ${invoice.date}")
            line("Store: ${invoice.store.name}")
            line("Customer: ${invoice.customer.name}")
            invoice.items.forEach { line("${it.productName} x${it.quantity}: ${it.unitPrice}") }
            line("Discount: ${invoice.discountAmount}")
            line("Tax: ${invoice.taxAmount}")
            line("Total: ${invoice.finalAmount}")
            document.finishPage(page)
            document.writeTo(output)
        } finally { document.close() }
    }

    private companion object { const val PAGE_WIDTH = 595; const val PAGE_HEIGHT = 842; const val PAGE_NUMBER = 1; const val LEFT_MARGIN = 48f; const val TOP_MARGIN = 72f; const val LINE_HEIGHT = 26f; const val TEXT_SIZE = 14f }
}

package ir.siamak.fintrack.store.core.pdf

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import ir.siamak.fintrack.store.domain.model.Invoice
import ir.siamak.fintrack.common.core.formatter.DateFormatter
import java.io.OutputStream
import java.time.LocalDate
import java.text.NumberFormat
import java.util.Locale

/** Persian, RTL, paginated invoice; uses only the persisted invoice snapshot. */
class InvoicePdfGenerator {
    fun generate(invoice: Invoice, output: OutputStream, logo: Bitmap? = null) {
        val document = PdfDocument()
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 13f; color = Color.BLACK; typeface = Typeface.create("sans-serif", Typeface.NORMAL) }
        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
        var y = 40f
        fun finish() {
            page.canvas.drawText(pageNumber.toString(), 290f, 820f, paint)
            document.finishPage(page)
        }
        fun nextPage() {
            finish(); pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()); y = 40f
        }
        fun line(text: String) {
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, 515)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL).setTextDirection(TextDirectionHeuristics.RTL)
                .setLineSpacing(4f, 1f).setIncludePad(true).build()
            // Draw whole layout lines; never clip a Persian baseline at the page boundary.
            for(index in 0 until layout.lineCount) {
                val top = layout.getLineTop(index)
                val bottom = layout.getLineBottom(index)
                val height = bottom - top
                if(y + height > 790) nextPage()
                page.canvas.save()
                page.canvas.translate(40f, y - top)
                page.canvas.clipRect(0f, top.toFloat(), 515f, bottom.toFloat())
                layout.draw(page.canvas)
                page.canvas.restore()
                y += height
            }
            y += 7f
        }
        fun money(value: Long) = NumberFormat.getIntegerInstance(Locale.forLanguageTag("fa")).format(value) + " تومان"
        fun date(value: String) = runCatching { DateFormatter.formatJalali(LocalDate.parse(value.take(10))) }.getOrDefault(value)
        try {
            logo?.let { bitmap ->
                val ratio = minOf(80f / bitmap.width, 60f / bitmap.height)
                page.canvas.drawBitmap(bitmap, null, RectF(475f, y, 475f + bitmap.width * ratio, y + bitmap.height * ratio), null)
                y += 70f
            }
            line(if(invoice.preOrderId != null) "فاکتور پیش‌سفارش" else "فاکتور فروش")
            line(invoice.store.name + " — " + invoice.store.brand)
            line(listOfNotNull(invoice.store.phone, invoice.store.address).joinToString(" | "))
            line("شماره: " + invoice.invoiceNumber)
            line("تاریخ: " + date(invoice.date))
            if(invoice.cancelled) line("لغوشده — دریافت‌های قبلی محفوظ است؛ استرداد وجه جداگانه پیگیری شود.")
            invoice.seller?.let { line("فروشنده: " + it.fullName + " | " + it.phone) }
            line("مشتری: " + invoice.customer.name + " | " + invoice.customer.phone)
            invoice.customer.address?.let { line("آدرس مشتری: " + it) }
            invoice.description?.let { line("شرح سفارش: " + it) }
            invoice.deliveryDate?.let { line("تاریخ تحویل: " + date(it)) }
            invoice.items.forEachIndexed { index, item ->
                line((index + 1).toString() + ". " + item.productName + " × " + item.quantity + " | واحد: " + money(item.unitPrice) + " | جمع: " + money(ir.siamak.fintrack.store.domain.usecase.sale.CalculateSaleAmountUseCase()(listOf(item))))
            }
            invoice.materialItems.forEach { line("ماده: " + it.materialName + " | مقدار: " + it.quantity + " | قیمت واحد: " + money(it.unitPrice)) }
            line("تخفیف: " + money(invoice.discountAmount))
            line("مالیات: " + money(invoice.taxAmount))

            invoice.pricing?.let {
                line("هزینه مواد: " + money(it.materialCost))
                line("تولید: " + money(it.productionCost) + " | سود: " + money(it.profit) + " | ارسال: " + money(it.shippingCost))
                line("قیمت پیشنهادی: " + money(it.suggestedPrice))
            }
            line("مبلغ نهایی: " + money(invoice.finalAmount))
            line("پرداخت‌شده: " + money(invoice.payment?.paidAmount ?: 0))
            line("مانده: " + money(ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoiceBalanceUseCase()(invoice)))
            invoice.payment?.installments?.forEach { line("قسط " + it.monthNumber + " | سود ماهانه " + it.percentage + "٪ | سررسید " + date(it.dueDate) + " | " + money(it.amount)) }
            line("با تشکر از خرید شما")
            finish()
            document.writeTo(output)
        } finally { document.close() }
    }
}

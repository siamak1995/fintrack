package ir.siamak.fintrack.store.core.pdf

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import ir.siamak.fintrack.common.core.formatter.*
import ir.siamak.fintrack.store.domain.model.StoreReport
import java.io.OutputStream

/** Exports the entire filtered result, not only the on-screen page. */
class StoreReportPdfGenerator {
    fun generate(report: StoreReport, output: OutputStream) {
        val doc = PdfDocument()
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 11f; typeface = Typeface.create("sans-serif", Typeface.NORMAL) }
        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNumber).create())
        var y = 42f
        fun finish() {
            page.canvas.drawText(PersianNumberFormatter.format(pageNumber.toLong()), 290f,820f,paint)
            doc.finishPage(page)
        }
        fun nextPage() {
            finish(); pageNumber++; page = doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNumber).create()); y = 42f
        }
        fun line(text: String) {
            val value = PersianNumberFormatter.toPersianDigits(text)
            val layout = StaticLayout.Builder.obtain(value,0,value.length,paint,511).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(TextDirectionHeuristics.RTL).setLineSpacing(4f,1f).build()
            for(i in 0 until layout.lineCount) {
                val top = layout.getLineTop(i); val bottom = layout.getLineBottom(i)
                if(y + bottom - top > 792f) {
                    nextPage()
                }
                page.canvas.save(); page.canvas.translate(42f,y-top); page.canvas.clipRect(0f,top.toFloat(),511f,bottom.toFloat()); layout.draw(page.canvas); page.canvas.restore()
                y += bottom - top
            }
            y += 8f
        }
        val columnWidths = listOf(104,66,130,72,70,69)
        val header = listOf("شماره","تاریخ","مشتری","قابل پرداخت","دریافت","مانده")
        fun tableRow(cells: List<String>, isHeader: Boolean) {
            val layouts = cells.mapIndexed { index,text ->
                val value = PersianNumberFormatter.toPersianDigits(text)
                StaticLayout.Builder.obtain(value,0,value.length,paint,columnWidths[index]-12)
                    .setTextDirection(TextDirectionHeuristics.RTL).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(3f,1f).build()
            }
            val maxLines = layouts.maxOf { it.lineCount }
            var first = 0
            while(first < maxLines) {
                if(y + 40 > 792) nextPage()
                var count = (maxLines-first).coerceAtLeast(1)
                fun height(lines: Int): Float = layouts.maxOf { layout ->
                    if(first >= layout.lineCount) 0 else layout.getLineBottom(minOf(first+lines,layout.lineCount)-1) - layout.getLineTop(first)
                } + 14f
                while(count > 1 && y + height(count) > 792) count--
                if(y + height(count) > 792) { nextPage(); continue }
                val rowHeight = height(count)
                var right = 553f
                val border = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(210,220,224); style = Paint.Style.STROKE; strokeWidth = .5f }
                val fill = Paint().apply { color = if(isHeader) Color.rgb(224,237,239) else if(first == 0) Color.rgb(249,251,252) else Color.WHITE }
                layouts.forEachIndexed { index,layout ->
                    val left = right - columnWidths[index]
                    page.canvas.drawRect(left,y,right,y+rowHeight,fill); page.canvas.drawRect(left,y,right,y+rowHeight,border)
                    if(first < layout.lineCount) {
                        val top = layout.getLineTop(first)
                        val bottom = layout.getLineBottom(minOf(first+count,layout.lineCount)-1)
                        page.canvas.save(); page.canvas.translate(left+6,y+7-top)
                        page.canvas.clipRect(0f,top.toFloat(),(columnWidths[index]-12).toFloat(),bottom.toFloat())
                        layout.draw(page.canvas); page.canvas.restore()
                    }
                    right = left
                }
                y += rowHeight; first += count
                if(first < maxLines) nextPage()
            }
        }
        try {
            paint.color = Color.rgb(25,65,78); paint.textSize = 20f
            line("گزارش حسابدار فروشگاه")
            paint.color = Color.BLACK; paint.textSize = 11f
            line(DateFormatter.formatJalali(report.filter.from) + " تا " + DateFormatter.formatJalali(report.filter.to))
            line("فیلتر مشتری: " + (report.filter.customerId?.toString() ?: "همه") + " | کالا: " + (report.filter.productId?.toString() ?: "همه"))
            line("وضعیت: " + when(report.filter.settlement) {
                ir.siamak.fintrack.store.domain.model.ReportSettlement.ALL -> "همه"
                ir.siamak.fintrack.store.domain.model.ReportSettlement.SETTLED -> "تسویه‌شده"
                ir.siamak.fintrack.store.domain.model.ReportSettlement.OUTSTANDING -> "دارای مانده"
                ir.siamak.fintrack.store.domain.model.ReportSettlement.INSTALLMENT -> "اقساطی"
            } + " | جست‌وجو: " + report.filter.search.ifBlank { "بدون محدودیت" })
            line("تعداد: " + report.totals.count + " | مبلغ قابل پرداخت: " + MoneyFormatter.format(report.totals.payable))
            line("دریافتی فعلی: " + MoneyFormatter.format(report.totals.received) + " | مانده: " + MoneyFormatter.format(report.totals.balance))
            line("تخفیف فاکتور: " + MoneyFormatter.format(report.totals.discount) + " | مالیات: " + MoneyFormatter.format(report.totals.tax))
            line("افزودهٔ اقساط: " + MoneyFormatter.format(report.totals.financeCharge))
            if(report.totals.unreconciledInvoices > 0) line("فاکتور با اقلام ناقص یا اختلاف مبلغ: " + report.totals.unreconciledInvoices +
                "؛ مبلغ قابل پرداخت از سند ذخیره‌شده خوانده شده است. اختلاف سند نقدی یا فاقد اقلام، سود اقساط نیست.")
            line("دریافتی‌ها وضعیت فعلی فاکتورهای این بازه‌اند، نه گردش وجه در تاریخ دریافت. فاکتورهای لغوشده از جمع‌ها کنار گذاشته شده‌اند.")
            if(report.invalidDates > 0) line("فاکتور دارای تاریخ غیرقابل‌تفسیر خارج از جمع دوره: " + report.invalidDates)
            if(report.filter.productId != null) line("مبالغ، کل فاکتورهای حاوی کالای انتخابی هستند؛ تخصیص ساختگی تخفیف و مالیات به کالا انجام نشده است.")
            line("دورهٔ مقایسه: " + DateFormatter.formatJalali(report.comparisonFrom) + " تا " + DateFormatter.formatJalali(report.comparisonTo) + " | " + MoneyFormatter.format(report.comparison.payable))
            line("جزئیات فاکتورها")
            tableRow(header,true)
            if(report.allRows.isEmpty()) line("هیچ فاکتوری مطابق فیلترها وجود ندارد.")
            report.allRows.forEach { invoice ->
                if(y + 100 > 792) { nextPage(); tableRow(header,true) }
                tableRow(listOf(invoice.invoiceNumber,DateFormatter.formatJalali(java.time.LocalDate.parse(invoice.date.take(10))),invoice.customer.name,
                    MoneyFormatter.format(invoice.finalAmount,""),MoneyFormatter.format(invoice.payment?.paidAmount ?: 0,""),MoneyFormatter.format(invoice.finalAmount - (invoice.payment?.paidAmount ?: 0),"")),false)
            }
            y += 14
            line("کالاها: نام | تعداد | مبلغ ناخالص پیش از تخفیف و مالیات")
            report.products.forEach { line(it.name + " | " + it.quantity + " | " + MoneyFormatter.format(it.gross)) }
            line("روند: دوره | مبلغ قابل پرداخت | تعداد فاکتور")
            report.trend.forEach { line(it.label + " | " + MoneyFormatter.format(it.amount) + " | " + it.count) }
            finish(); doc.writeTo(output)
        } finally { doc.close() }
    }
}

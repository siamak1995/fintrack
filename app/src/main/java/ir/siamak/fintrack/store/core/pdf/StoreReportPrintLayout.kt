package ir.siamak.fintrack.store.core.pdf

import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.common.core.formatter.*
import ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode
import java.time.LocalDate

/** A4 reporting layout shared by Android rendering and offline visual review. */
class StoreReportPrintLayout {
    fun create(report: StoreReport): InvoicePrintDocument {
        val pages = mutableListOf<List<InvoicePrintElement>>()
        var elements = mutableListOf<InvoicePrintElement>()
        var y = 0f
        val ink = 0xFF173D46.toInt()
        val light = 0xFFF0F5F6.toInt()
        fun text(value: String,x: Float,top: Float,width: Float,size: Float = 9f,bold: Boolean = false,color: Int = -16777216,align: String = "right") {
            elements += InvoicePrintElement("text",x,top,width,text = PersianNumberFormatter.display(value),size = size,bold = bold,color = color,align = align)
        }
        fun fill(x: Float,top: Float,width: Float,height: Float,color: Int) { elements += InvoicePrintElement("fill",x,top,width,height,color = color) }
        fun line(top: Float) { elements += InvoicePrintElement("line",32f,top,531f) }
        fun amount(value: Long) = PersianNumberFormatter.format(value)
        fun money(value: Long) = amount(value) + " تومان"
        fun wrap(value: String,width: Float,size: Float): List<String> {
            if(Regex("[0-9۰-۹]{4}/[0-9۰-۹]{2}/[0-9۰-۹]{2}").matches(value)) return listOf(value)
            val count = (width/(size*.65f)).toInt().coerceAtLeast(4)
            return value.split('\n').flatMap { paragraph ->
                val result = mutableListOf<String>(); var current = ""
                paragraph.split(' ').forEach { word -> word.chunked(count).forEach { part ->
                    if(current.isNotEmpty() && current.length+part.length+1 > count) { result += current; current = "" }
                    current += (if(current.isEmpty()) "" else " ") + part
                } }
                if(current.isNotEmpty()) result += current
                result.ifEmpty { listOf("") }
            }
        }
        val storeName = report.stores.firstOrNull { it.id == report.filter.storeId }?.name ?: "همه فروشگاه‌ها و نمایشگاه‌ها"
        fun header() {
            fill(0f,0f,595f,102f,ink)
            text(if(report.filter.document == ReportDocument.SALE) "گزارش فروش" else "گزارش پیش‌سفارش",32f,23f,531f,20f,true,-1)
            text(storeName,32f,54f,531f,11f,false,-1)
            text(DateFormatter.formatJalali(report.filter.from) + if(report.filter.from == report.filter.to) " · گزارش روزانه" else " تا " + DateFormatter.formatJalali(report.filter.to),32f,77f,531f,9f,false,-1)
            y = 122f
        }
        fun finish() {
            line(803f)
            text("حسابدار · گزارش عملکرد",32f,813f,400f,8f,color = ink)
            text("صفحه " + (pages.size+1),32f,813f,531f,8f,align = "left")
            pages += elements.toList(); elements = mutableListOf()
        }
        fun next() { finish(); header() }
        fun paragraph(value: String,size: Float = 9f,bold: Boolean = false) {
            wrap(value,531f,size).forEach { row ->
                if(y+size+8 > 786f) next()
                text(row,32f,y,531f,size,bold); y += size+7f
            }; y += 8f
        }
        fun heading(value: String) { if(y+132 > 786f) next(); y += 9f; text(value,32f,y,531f,13f,true,ink); y += 28f }
        fun table(headers: List<String>,widths: List<Float>,rows: List<List<String>>) {
            fun row(cells: List<String>,heading: Boolean,index: Int) {
                val lines = cells.mapIndexed { i,s -> wrap(s,widths[i]-14f,if(heading) 8.5f else 8f) }
                var offset = 0
                val max = lines.maxOf { it.size }
                while(offset < max) {
                    val available = ((786f-y-14)/14).toInt().coerceAtLeast(0)
                    if(available < 1) { next(); if(!heading) row(headers,true,0); continue }
                    val count = minOf(max-offset,available)
                    val height = count*14f+14f
                    fill(32f,y,531f,height,if(heading) ink else if(index%2 == 0) light else -1)
                    var right = 563f
                    lines.forEachIndexed { i,values ->
                        val left = right-widths[i]
                        values.drop(offset).take(count).forEachIndexed { n,value -> text(value,left+7,y+7+n*14,widths[i]-14,if(heading) 8.5f else 8f,heading,if(heading) -1 else -16777216) }
                        right = left
                    }
                    y += height; line(y); offset += count
                    if(offset < max) { next(); if(!heading) row(headers,true,0) }
                }
            }
            row(headers,true,0)
            rows.forEachIndexed { i,cells -> if(y+44 > 786) { next(); row(headers,true,0) }; row(cells,false,i) }
            if(rows.isEmpty()) paragraph("داده‌ای مطابق فیلتر انتخاب‌شده وجود ندارد.")
            y += 12f
        }
        header()
        heading("خلاصه عملکرد")
        table(listOf("تعداد فاکتور", "مبلغ فروش · تومان", "دریافت‌شده · تومان", "مانده · تومان"),listOf(96f,145f,145f,145f),
            listOf(listOf(amount(report.totals.count.toLong()),amount(report.totals.payable),amount(report.totals.received),amount(report.totals.balance))))
        paragraph("تخفیف: " + money(report.totals.discount) + "  ·  مالیات: " + money(report.totals.tax) + "  ·  افزوده اقساط: " + money(report.totals.financeCharge))
        val settlement = when(report.filter.settlement) { ReportSettlement.ALL -> "همه"; ReportSettlement.SETTLED -> "تسویه‌شده"; ReportSettlement.OUTSTANDING -> "دارای مانده"; ReportSettlement.INSTALLMENT -> "اقساطی" }
        val customer = report.allRows.firstOrNull { it.customer.id == report.filter.customerId }?.customer?.name
        val product = report.allRows.flatMap { it.items }.firstOrNull { it.productId == report.filter.productId }?.productName
        paragraph("فیلترها · تسویه: " + settlement + " · مشتری: " + (customer ?: if(report.filter.customerId == null) "همه" else "انتخاب‌شده") + " · کالا: " + (product ?: if(report.filter.productId == null) "همه" else "انتخاب‌شده") + if(report.filter.search.isBlank()) "" else " · جست‌وجو: " + report.filter.search)
        heading("عملکرد فروشگاه / نمایشگاه")
        table(listOf("فروشگاه", "فروش · تومان", "دریافت · تومان", "مانده · تومان"),listOf(162f,123f,123f,123f),report.branches.map { listOf(it.name,amount(it.totals.payable),amount(it.totals.received),amount(it.totals.balance)) })
        heading("جزئیات فاکتورها")
        table(listOf("کد رهگیری", "تاریخ", "نام مشتری", "فروش", "دریافت", "مانده"),listOf(103f,65f,117f,82f,82f,82f),report.allRows.map {
            listOf(InvoiceTrackingCode.of(it),DateFormatter.formatJalali(LocalDate.parse(it.date.take(10))),it.customer.name,amount(it.finalAmount),amount(it.payment?.paidAmount ?: 0),amount(it.finalAmount-(it.payment?.paidAmount ?: 0)))
        })
        heading("مقایسه عملکرد")
        paragraph((report.stores.firstOrNull { it.id == report.filter.comparisonStoreId }?.name ?: "دوره مقایسه") + " · " + DateFormatter.formatJalali(report.comparisonFrom) + " تا " + DateFormatter.formatJalali(report.comparisonTo))
        table(listOf("مبلغ فروش", "دریافت‌شده", "مانده"),listOf(177f,177f,177f),listOf(listOf(money(report.comparison.payable),money(report.comparison.received),money(report.comparison.balance))))
        if(report.products.isNotEmpty()) { heading("عملکرد کالاها · پیش از تخفیف و مالیات"); table(listOf("کالا", "تعداد", "مبلغ ناخالص · تومان"),listOf(241f,100f,190f),report.products.map { listOf(it.name,amount(it.quantity),amount(it.gross)) }) }
        heading("روند فروش")
        table(listOf("دوره", "تعداد فاکتور", "فروش · تومان"),listOf(177f,177f,177f),report.trend.map { listOf(it.label,amount(it.count.toLong()),amount(it.amount)) })
        heading("روند فروش دوره / فروشگاه مقایسه")
        table(listOf("دوره", "تعداد فاکتور", "فروش · تومان"),listOf(177f,177f,177f),report.comparisonTrend.map { listOf(it.label,amount(it.count.toLong()),amount(it.amount)) })
        heading("مبنای گزارش")
        paragraph("دریافت و مانده، وضعیت فعلی فاکتورهای صادرشده در بازه‌اند؛ این گزارش گردش وجه به تفکیک تاریخ دریافت نیست. اسناد لغوشده از جمع فروش کنار گذاشته شده‌اند.")
        if(report.filter.productId != null) paragraph("فیلتر کالا، کل فاکتورهای حاوی آن کالا را انتخاب می‌کند؛ تخفیف و مالیات به کالا تخصیص داده نشده است.")
        if(report.totals.unreconciledInvoices > 0) paragraph("اسناد دارای اقلام ناقص یا اختلاف مبلغ: " + report.totals.unreconciledInvoices + "؛ مبلغ فروش از سند ذخیره‌شده خوانده شده است.")
        if(report.invalidDates > 0) paragraph("اسناد با تاریخ نامعتبر خارج از جمع دوره: " + report.invalidDates)
        finish()
        return InvoicePrintDocument(pages,595,842)
    }
}

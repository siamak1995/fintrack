package ir.siamak.fintrack.store.core.pdf

import ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.common.core.formatter.DateFormatter
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.text.NumberFormat
import java.util.Locale

/** Platform-independent A5 geometry measured from the supplied reference (420 × 595 pt). */
@Serializable data class InvoicePrintElement(val kind: String, val x: Float, val y: Float, val width: Float = 0f,
    val height: Float = 0f, val text: String = "", val size: Float = 9f, val bold: Boolean = false, val align: String = "right", val color: Int = -16777216)
@Serializable data class InvoicePrintDocument(val pages: List<List<InvoicePrintElement>>, val width: Int = 420, val height: Int = 595)

class InvoicePrintLayout {
    fun create(invoice: Invoice, related: List<Invoice>? = null): InvoicePrintDocument {
        val pages = mutableListOf<MutableList<InvoicePrintElement>>()
        var elements = mutableListOf<InvoicePrintElement>()
        val overflowNotes = mutableListOf<String>()
        val tracking = InvoiceTrackingCode.of(invoice)
        fun text(value: String, x: Float, y: Float, width: Float, size: Float = 9f, bold: Boolean = false, align: String = "right") {
            elements += InvoicePrintElement("text",x,y,width,text = PersianNumberFormatter.toPersianDigits(value),size = size,bold = bold,align = align)
        }
        fun rule(x: Float,y: Float,width: Float) { elements += InvoicePrintElement("line",x,y,width) }
        fun box(x: Float,y: Float,width: Float,height: Float) { elements += InvoicePrintElement("box",x,y,width,height) }
        fun image(kind: String,x: Float,y: Float,width: Float,height: Float) { elements += InvoicePrintElement(kind,x,y,width,height) }
        fun amount(value: Long) = NumberFormat.getIntegerInstance(Locale.US).format(value)
        fun money(value: Long) = amount(value) + " تومان"
        fun date(value: String) = runCatching { DateFormatter.formatJalali(LocalDate.parse(value.take(10))) }.getOrDefault(value)
        fun lines(value: String, width: Float, size: Float = 9f): List<String> {
            val limit = (width/(size*.58f)).toInt().coerceAtLeast(1)
            val result = mutableListOf<String>(); var line = ""
            value.split('\n').forEach { paragraph ->
                paragraph.split(' ').forEach { word ->
                    if(line.isNotEmpty() && line.length + word.length + 1 > limit) { result += line; line = "" }
                    word.chunked(limit).forEachIndexed { i, chunk ->
                        if(i > 0 && line.isNotEmpty()) { result += line; line = "" }
                        line += (if(line.isEmpty()) "" else " ") + chunk
                    }
                }
                if(line.isNotEmpty()) { result += line; line = "" }
            }
            return result.ifEmpty { listOf("") }
        }
        fun paragraph(value: String,x: Float,y: Float,width: Float,size: Float = 9f,bold: Boolean = false,align: String = "right",maxLines: Int = Int.MAX_VALUE): Float {
            val all = lines(value,width,size)
            val wrapped = if(all.size <= maxLines) all else {
                if(value !in overflowNotes) overflowNotes += value
                all.take(maxLines-1) + "ادامه در پیوست"
            }
            wrapped.forEachIndexed { index,line -> text(line,x,y+index*(size+4),width,size,bold,align) }
            return wrapped.size*(size+4)
        }
        fun close() {
            text("صفحه " + (pages.size+1),15f,574f,100f,8f,align = "left")
            pages += elements; elements = mutableListOf()
        }
        var headerBottom = 118f
        fun header() {
            text(if(invoice.preOrderId == null) "فاکتور فروش" else "فاکتور پیش‌سفارش",105f,22f,210f,17f,true,"center")
            val nameHeight = paragraph(invoice.seller?.fullName ?: invoice.store.name,95f,57f,230f,13f,align = "center",maxLines = 2)
            val identity = if(invoice.seller == null) invoice.store.brand else listOf(invoice.store.name,invoice.store.brand).filter(String::isNotBlank).joinToString(" · ")
            val identityY = 57f+nameHeight+4f
            val identityHeight = if(identity.isBlank()) 0f else paragraph(identity,75f,identityY,270f,8f,align = "center",maxLines = 2)
            val addressY = maxOf(93f,identityY+identityHeight+3f)
            val addressHeight = paragraph("آدرس: " + (invoice.seller?.address ?: invoice.store.address ?: "—"),75f,addressY,270f,9f,align = "center",maxLines = 2)
            val contact = invoice.seller?.phone?.takeIf(String::isNotBlank) ?: invoice.store.phone?.takeIf(String::isNotBlank)
            if(contact != null) text("تلفن: " + contact,75f,addressY+addressHeight,270f,7f,align = "center")
            image("logo",365f,28f,34f,34f); image("qr",18f,18f,57f,57f)
            headerBottom = maxOf(118f,addressY+addressHeight+(if(contact == null) 3f else 12f))
            rule(15f,headerBottom,390f)
        }
        header()
        val shift = headerBottom-118f
        text("شماره فاکتور: " + tracking,224f,129f+shift,170f,8f)
        text("تاریخ فاکتور: " + date(invoice.date),224f,146f+shift,170f)
        val status = if(invoice.cancelled) "لغوشده" else when(invoice.paymentStatus) {
            Invoice.PaymentStatus.SETTLED -> "تسویه‌شده"; Invoice.PaymentStatus.UNSETTLED -> "تسویه‌نشده"; Invoice.PaymentStatus.INSTALLMENT -> "اقساطی"
        }
        text("وضعیت: " + status,224f,163f+shift,170f,9f,true)
        text("تاریخ تسویه: " + (invoice.payment?.installments?.firstOrNull()?.dueDate?.let(::date) ?: "ثبت نشده"),224f,180f+shift,170f)
        val customerHeight = paragraph("طرف حساب: " + invoice.customer.name,15f,130f+shift,155f,10f,true,maxLines = 2)
        val customerAddressY = maxOf(153f+shift,136f+shift+customerHeight)
        val customerAddressHeight = paragraph("آدرس: " + (invoice.customer.address ?: "—"),15f,customerAddressY,155f,maxLines = 2)
        val phoneY = maxOf(180f+shift,customerAddressY+customerAddressHeight+6f)
        val phoneHeight = paragraph("شماره تماس: " + invoice.customer.phone,15f,phoneY,155f,maxLines = 2)
        var y = maxOf(209f+shift,phoneY+phoneHeight+12f)
        fun tableHeader() {
            box(15f,y,390f,18f)
            listOf(77.4f,139.8f,202.2f,249f,385.5f).forEach { elements += InvoicePrintElement("vertical",it,y,height = 18f) }
            text("جمع",16f,y+3,60f,9f,true,"center"); text("تخفیف",78f,y+3,60f,9f,true,"center")
            text("مبلغ (تومان)",141f,y+3,60f,8f,true,"center"); text("تعداد",203f,y+3,45f,9f,true,"center")
            text("توضیح",250f,y+3,134f,9f,true); y += 18f
        }
        tableHeader()
        fun next(withTable: Boolean = true) { close(); header(); y = headerBottom+14f; if(withTable) tableHeader() }
        invoice.items.forEachIndexed { index,item ->
            val wrapped = lines(item.productName,128f)
            var offset = 0
            while(offset < wrapped.size) {
                if(y+20 > 520) next()
                val available = ((520-y-6)/13).toInt().coerceAtLeast(1)
                val part = wrapped.drop(offset).take(available)
                val height = maxOf(18f,part.size*13f+6)
                box(15f,y,390f,height)
                part.forEachIndexed { line,value -> text(value,252f,y+3+line*13,130f) }
                if(offset == 0) {
                    text((index+1).toString(),386f,y+3,17f,8f,align = "center")
                    text(amount(item.quantity.toLong()) + " عدد",203f,y+3,45f,8f,align = "center")
                    text(amount(item.unitPrice),141f,y+3,60f,8f,align = "center")
                    text("—",78f,y+3,60f,8f,align = "center")
                    text(amount(Math.multiplyExact(item.unitPrice,item.quantity.toLong())),16f,y+3,60f,8f,align = "center")
                }
                y += height; offset += part.size
                if(offset < wrapped.size) next()
            }
        }
        if(invoice.items.isEmpty()) { box(15f,y,390f,20f); text("جزئیات اقلام در این سند ثبت نشده است",20f,y+3,380f); y += 20f }
        if(y+268 > 565) next(false)
        y += 5f
        val summaryTop = y
        val gross = invoice.items.fold(0L) { sum,item -> Math.addExact(sum,Math.multiplyExact(item.unitPrice,item.quantity.toLong())) }
        val base = Math.addExact(Math.subtractExact(gross,invoice.discountAmount),invoice.taxAmount)
        val finance = if(invoice.payment?.type == Payment.PaymentType.INSTALLMENT && invoice.items.isNotEmpty() && invoice.finalAmount >= base && base >= 0) invoice.finalAmount-base else 0
        box(15f,y,156f,65f)
        fun total(label: String,value: String,top: Float,bold: Boolean = false) {
            text(label,96f,top,71f,9f,bold); text(value,20f,top,80f,8.2f,bold,"left")
        }
        total("جمع",money(gross),y+6); total("جمع تخفیف‌ها", "—", y+27)
        total(if(invoice.tax.percent == 0 && invoice.taxAmount > 0) "مالیات" else "٪ " + invoice.tax.percent + " مالیات",money(invoice.taxAmount),y+47)
        val outstanding = related?.filter { it.store.id == invoice.store.id && !it.cancelled &&
            it.customer.id == invoice.customer.id && (invoice.customer.id > 0 || it.invoiceNumber == invoice.invoiceNumber) &&
            it.finalAmount > (it.payment?.paidAmount ?: 0) }
        val balance = ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoiceBalanceUseCase()(invoice)
        val outstandingText = if(outstanding != null) "[[ مانده فاکتورهای تسویه‌نشده: " + outstanding.size + " مورد به جمع " + money(outstanding.fold(0L) { sum,row -> Math.addExact(sum, row.finalAmount-(row.payment?.paidAmount ?: 0)) }) + " می‌باشد ]]"
            else "مانده این فاکتور: " + money(balance)
        paragraph(outstandingText,188f,y+9,215f,8.5f,true,maxLines = 4)
        total("جمع کل",money(if(invoice.items.isEmpty()) invoice.finalAmount else Math.addExact(gross,invoice.taxAmount)),y+74)
        total("تخفیف فاکتور",money(invoice.discountAmount),y+95); total("کسر رند","—",y+116)
        var extra = 0f
        if(finance > 0) { total("افزوده اقساط",money(finance),y+137); extra = 21f }
        rule(15f,y+130+extra,156f); total("قابل پرداخت",money(invoice.finalAmount),y+137+extra,true)
        box(216f,y+65f,177f,95f); rule(216f,y+88f,177f)
        text("اطلاعات پرداخت",220f,y+71f,153f,9f,true)
        box(378f,y+72f,11f,8f); rule(378f,y+74f,11f)
        text("شماره کارت: " + invoice.seller?.cardNumber.orEmpty(),222f,y+106f,164f,8.5f)
        text("شماره حساب: " + invoice.seller?.accountNumber.orEmpty(),222f,y+130f,164f,8.5f)
        text("دریافت: " + money(invoice.payment?.paidAmount ?: 0) + " | مانده: " + money(balance),222f,y+147f,164f,7f)
        val signY = maxOf(449f,summaryTop+190f+extra)
        text("امضای خریدار",322f,signY,75f,9f,true,"center"); text("امضای فروشنده",212f,signY,90f,9f,true,"center")
        text("با نهایت تشکر",20f,signY,105f,9f,align = "center")
        image("signature",220f,signY+17f,90f,minOf(67f,565f-signY-17f))
        image("logo",140f,signY+40f,40f,minOf(60f,565f-signY-40f).coerceAtLeast(1f))
        if(invoice.cancelled || invoice.description != null || invoice.pricing != null || overflowNotes.isNotEmpty() || invoice.materialItems.isNotEmpty() || invoice.payment?.installments.orEmpty().isNotEmpty()) {
            next(false)
            text("پیوست و جزئیات فاکتور " + tracking,15f,y,390f,10f,true); y += 24f
            val notes = overflowNotes.toList() + listOfNotNull(if(invoice.cancelled) "لغوشده — دریافت‌های قبلی محفوظ است؛ استرداد وجه جداگانه پیگیری شود." else null, invoice.description, invoice.deliveryDate?.let { "تاریخ تحویل: " + date(it) }) +
                invoice.materialItems.map { "ماده: " + it.materialName + " | مقدار: " + NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 8 }.format(it.quantity) + " | قیمت واحد: " + money(it.unitPrice) } +
                invoice.pricing?.let { listOf("هزینه مواد: " + money(it.materialCost), "تولید: " + money(it.productionCost), "سود: " + money(it.profit), "ارسال: " + money(it.shippingCost), "قیمت پیشنهادی: " + money(it.suggestedPrice)) }.orEmpty() +
                invoice.payment?.installments.orEmpty().map { "قسط " + it.monthNumber + " | سررسید: " + date(it.dueDate) + " | سود ماهانه: " + it.percentage + "٪ | مبلغ: " + money(it.amount) }
            notes.forEach { note -> lines(note,390f).forEach { line -> if(y > 545) next(false); text(line,15f,y,390f); y += 14f }; y += 8f }
        }
        close()
        return InvoicePrintDocument(pages)
    }
}

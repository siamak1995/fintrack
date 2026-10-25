package ir.siamak.fintrack.store.core.formatter

import ir.siamak.fintrack.store.domain.model.Invoice
import ir.siamak.fintrack.common.core.formatter.DateFormatter
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import java.security.MessageDigest
import java.time.LocalDate
import java.util.Locale

/** Public reference, independent of Room row ids and mutable payment/settlement state. */
object InvoiceTrackingCode {
    fun of(invoice: Invoice): String {
        val date = runCatching { PersianNumberFormatter.normalize(DateFormatter.formatJalali(LocalDate.parse(invoice.date.take(10)))).filter(Char::isDigit) }
            .getOrDefault(invoice.date.filter(Char::isDigit).take(8)).padEnd(8, '0').take(8)
        val identity = listOf(invoice.invoiceNumber, invoice.date, invoice.store.id.toString(), invoice.customer.phone,
            invoice.items.joinToString(";") { "${it.productId}:${it.quantity}:${it.unitPrice}" }).joinToString("|")
        val digest = MessageDigest.getInstance("SHA-256").digest(identity.toByteArray(Charsets.UTF_8))
            .take(8).joinToString("") { "%02X".format(Locale.ROOT, it.toInt() and 255) }
        return (if(invoice.preOrderId != null) "P" else "S") + "-" + date + "-" + digest
    }
}

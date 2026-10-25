package ir.siamak.fintrack.store.core.formatter

import ir.siamak.fintrack.store.domain.model.Invoice
import ir.siamak.fintrack.common.core.formatter.DateFormatter
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import java.time.LocalDate

/** Sequential public reference; the internal request UUID and settlement state remain unchanged. */
object InvoiceTrackingCode {
    fun of(invoice: Invoice): String {
        val date = runCatching { PersianNumberFormatter.normalize(DateFormatter.formatJalali(LocalDate.parse(invoice.date.take(10)))).filter(Char::isDigit) }
            .getOrDefault(invoice.date.filter(Char::isDigit).take(8)).padEnd(8, '0').take(8)
        val sequence = invoice.displaySequence.takeIf { it > 0 } ?: invoice.preOrderId ?: invoice.saleId ?: invoice.id
        return (if(invoice.preOrderId != null) "P" else "S") + "-" + date + "-" + sequence.toString().padStart(4,'0')
    }
}

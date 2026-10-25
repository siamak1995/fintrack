package ir.siamak.fintrack.store.domain.usecase.store

import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import ir.siamak.fintrack.store.domain.model.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import javax.inject.Inject

/** One row per persisted invoice. Receipts are current cumulative balances, not cash flow. */
class CalculateStoreReportUseCase @Inject constructor() {
    operator fun invoke(invoices: List<Invoice>, filter: StoreReportFilter, today: LocalDate = LocalDate.now()): StoreReport {
        require(!filter.to.isBefore(filter.from)) { "بازهٔ تاریخ معتبر نیست" }
        require(filter.page >= 0 && filter.pageSize in 1..100)
        require((filter.comparisonFrom == null) == (filter.comparisonTo == null))
        val days = ChronoUnit.DAYS.between(filter.from, filter.to) + 1
        val previousTo = filter.comparisonTo ?: filter.from.minusDays(1)
        val previousFrom = filter.comparisonFrom ?: previousTo.minusDays(days - 1)
        require(!previousTo.isBefore(previousFrom)) { "بازهٔ مقایسه معتبر نیست" }
        val search = PersianNumberFormatter.normalize(filter.search.trim()).lowercase()
        fun matching(from: LocalDate, to: LocalDate, includeCancelled: Boolean = false) = invoices.filter { invoice ->
            val date = runCatching { LocalDate.parse(invoice.date.take(10)) }.getOrNull()
            val paid = invoice.payment?.paidAmount ?: 0
            val remaining = Math.subtractExact(invoice.finalAmount, paid)
            date != null && !date.isBefore(from) && !date.isAfter(to) &&
                (includeCancelled || !invoice.cancelled) &&
                (invoice.preOrderId != null) == (filter.document == ReportDocument.PREORDER) &&
                (filter.customerId == null || invoice.customer.id == filter.customerId) &&
                (filter.productId == null || invoice.items.any { it.productId == filter.productId }) &&
                when(filter.settlement) {
                    ReportSettlement.ALL -> true
                    ReportSettlement.SETTLED -> remaining == 0L
                    ReportSettlement.OUTSTANDING -> remaining > 0
                    ReportSettlement.INSTALLMENT -> invoice.payment?.type == Payment.PaymentType.INSTALLMENT
                } && (search.isBlank() || listOf(invoice.invoiceNumber, ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode.of(invoice), invoice.customer.name, invoice.customer.phone,
                    invoice.description.orEmpty(), invoice.items.joinToString { it.productName })
                    .any { PersianNumberFormatter.normalize(it).lowercase().contains(search) })
        }
        val selected = matching(filter.from, filter.to)
        val ordered = when(filter.order) {
            ReportOrder.NEWEST -> selected.sortedWith(compareByDescending<Invoice> { it.date }.thenByDescending { it.id })
            ReportOrder.OLDEST -> selected.sortedWith(compareBy<Invoice> { it.date }.thenBy { it.id })
            ReportOrder.AMOUNT_DESC -> selected.sortedByDescending { it.finalAmount }
            ReportOrder.BALANCE_DESC -> selected.sortedByDescending { it.finalAmount - (it.payment?.paidAmount ?: 0) }
        }
        val pages = ((ordered.size + filter.pageSize - 1) / filter.pageSize).coerceAtLeast(1)
        val page = filter.page.coerceAtMost(pages - 1)
        val items = selected.flatMap { it.items }.filter { filter.productId == null || it.productId == filter.productId }
        return StoreReport(filter.copy(page = page), totals(selected, today), totals(matching(previousFrom, previousTo), today), previousFrom, previousTo,
            ordered.drop(page * filter.pageSize).take(filter.pageSize), ordered,
            items.groupBy { it.productId }.map { (id, rows) -> StoreReportProduct(id, rows.last().productName,
                rows.fold(0L) { a, b -> Math.addExact(a, b.quantity.toLong()) },
                rows.fold(0L) { a, b -> Math.addExact(a, Math.multiplyExact(b.unitPrice, b.quantity.toLong())) }) }
                .sortedByDescending { it.gross },
            selected.groupBy { it.customer.id }.map { (id, rows) ->
                val summary = totals(rows, today)
                StoreReportCustomer(id, rows.last().customer.name, rows.last().customer.phone, rows.size, summary.payable, summary.balance)
            }.sortedByDescending { it.payable },
            selected.groupBy { bucket(LocalDate.parse(it.date.take(10)), filter.period) }.toSortedMap().map { (label, rows) ->
                StoreReportBucket(label, rows.fold(0L) { a, b -> Math.addExact(a, b.finalAmount) }, rows.size)
            }, pages, matching(filter.from, filter.to, true).count { it.cancelled },
            invoices.count { (it.preOrderId != null) == (filter.document == ReportDocument.PREORDER) && runCatching { LocalDate.parse(it.date.take(10)) }.isFailure })
    }

    private fun totals(rows: List<Invoice>, today: LocalDate): StoreReportTotals {
        fun sum(selector: (Invoice) -> Long) = rows.fold(0L) { total, row -> Math.addExact(total, selector(row)) }
        fun gross(row: Invoice) = row.items.fold(0L) { a, item -> Math.addExact(a, Math.multiplyExact(item.unitPrice, item.quantity.toLong())) }
        fun base(row: Invoice) = Math.addExact(Math.subtractExact(gross(row), row.discountAmount), row.taxAmount)
        fun finance(row: Invoice): Long {
            // An undocumented legacy difference is not evidence of installment interest.
            if(row.preOrderId != null || row.payment?.type != Payment.PaymentType.INSTALLMENT || row.items.isEmpty()) return 0
            val principal = base(row)
            return if(principal >= 0 && row.finalAmount >= principal) Math.subtractExact(row.finalAmount, principal) else 0
        }
        val payable = sum { it.finalAmount }
        val paid = sum { it.payment?.paidAmount ?: 0 }
        return StoreReportTotals(rows.size, sum(::gross), sum { it.discountAmount }, sum { it.taxAmount },
            sum(::finance),
            payable, paid, Math.subtractExact(payable, paid), rows.count { row ->
                row.finalAmount > (row.payment?.paidAmount ?: 0) && row.payment?.installments.orEmpty().any {
                    runCatching { LocalDate.parse(it.dueDate).isBefore(today) }.getOrDefault(false)
                }
            }, rows.count { row -> row.preOrderId == null &&
                ((row.items.isEmpty() && row.finalAmount != 0L) || row.finalAmount != Math.addExact(base(row), finance(row))) })
    }

    private fun bucket(date: LocalDate, period: ReportPeriod): String {
        val day = if(period == ReportPeriod.WEEK) date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)) else date
        val jalali = JalaliDateConverter.fromGregorian(day)
        return when(period) {
            ReportPeriod.DAY, ReportPeriod.WEEK -> "%04d/%02d/%02d".format(java.util.Locale.ROOT, jalali.year, jalali.month, jalali.day)
            ReportPeriod.MONTH -> "%04d/%02d".format(java.util.Locale.ROOT, jalali.year, jalali.month)
            ReportPeriod.YEAR -> jalali.year.toString()
        }
    }
}

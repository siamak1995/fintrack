package ir.siamak.fintrack.personalaccountant.domain.report.usecases

import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import java.math.BigDecimal
import java.time.*
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class VisualReportBucket(val from: LocalDate, val to: LocalDate, val income: BigDecimal, val expense: BigDecimal, val previousIncome: BigDecimal)
data class VisualReport(val from: LocalDate, val to: LocalDate, val previousFrom: LocalDate, val previousTo: LocalDate, val buckets: List<VisualReportBucket>, val transactionCount: Int)

class CalculateVisualReportUseCase @Inject constructor() {
    operator fun invoke(transactions: List<Transaction>, from: LocalDate, to: LocalDate, zone: ZoneId = ZoneId.systemDefault()): VisualReport {
        require(!to.isBefore(from))
        val days = ChronoUnit.DAYS.between(from,to) + 1
        val count = minOf(5L,days).toInt()
        val previousFrom = from.minusDays(days)
        val dated = transactions.filter { it.type != TransactionType.TRANSFER }.map { it to Instant.ofEpochMilli(it.date).atZone(zone).toLocalDate() }
        fun sum(start: LocalDate, end: LocalDate, type: TransactionType) = dated.filter { (transaction,date) -> transaction.type == type && !date.isBefore(start) && !date.isAfter(end) }
            .fold(BigDecimal.ZERO) { total, (transaction,_) -> total.add(BigDecimal.valueOf(transaction.amount)) }
        val buckets = (0 until count).map { index ->
            val startOffset = index * days / count
            val endOffset = (index + 1) * days / count - 1
            val start = from.plusDays(startOffset); val end = from.plusDays(endOffset)
            VisualReportBucket(start,end,sum(start,end,TransactionType.INCOME),sum(start,end,TransactionType.EXPENSE),
                sum(previousFrom.plusDays(startOffset),previousFrom.plusDays(endOffset),TransactionType.INCOME))
        }
        return VisualReport(from,to,previousFrom,from.minusDays(1),buckets,
            dated.count { (_,date) -> !date.isBefore(from) && !date.isAfter(to) })
    }
}

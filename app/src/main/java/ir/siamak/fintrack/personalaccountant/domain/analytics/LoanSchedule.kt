package ir.siamak.fintrack.personalaccountant.domain.analytics

import ir.siamak.fintrack.personalaccountant.data.model.Installment
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*

data class LoanSchedule(val paidCount: Int, val remainingCount: Int, val remainingAmount: Double,
    val nextDue: LocalDate?, val end: LocalDate, val nextAmount: Double)

/** Cumulative rounding keeps the last payment exact, including loans indivisible by the count. */
fun loanSchedule(loan: Installment, zone: ZoneId = ZoneId.systemDefault()): LoanSchedule {
    val count = loan.installmentCount.coerceIn(1,600)
    val total = BigDecimal.valueOf(loan.totalAmount.coerceAtLeast(0.0))
    val paid = BigDecimal.valueOf(loan.paidAmount.coerceIn(0.0,loan.totalAmount.coerceAtLeast(0.0)))
    val payment = loan.installmentAmount?.takeIf { it.isFinite() && it > 0 }?.let(BigDecimal::valueOf)
    fun cumulative(index: Int) = if(index == count) total else if(payment != null)
        payment.multiply(BigDecimal(index)).min(total) else total.multiply(BigDecimal(index)).divide(BigDecimal(count), 2, RoundingMode.HALF_UP)
    val completed = (1..count).count { cumulative(it) <= paid }
    val first = Instant.ofEpochMilli(loan.dueDate).atZone(zone).toLocalDate()
    fun due(index: Int) = if(loan.installmentAmount == null && loan.endDate == null) first.plusMonths(index.toLong()) else loanDueDate(first,index)
    return LoanSchedule(completed, count-completed, total.subtract(paid).toDouble(),
        if(completed == count) null else due(completed),
        loan.endDate?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() } ?: due(count-1),
        if(completed == count) 0.0 else cumulative(completed+1).subtract(paid).toDouble())
}

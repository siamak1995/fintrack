package ir.siamak.fintrack.personalaccountant

import ir.siamak.fintrack.personalaccountant.data.model.*
import ir.siamak.fintrack.personalaccountant.domain.report.usecases.CalculateVisualReportUseCase
import java.time.*
import org.junit.Assert.*
import org.junit.Test

class VisualReportTest {
    @Test fun realTransactionsAndPreviousPeriodAreUsedWithoutCountingTransfers() {
        fun row(id: Long, day: String, type: TransactionType, amount: Double) = Transaction(id=id,amount=amount,type=type,
            categoryName="",walletId=1,memberId=1,date=LocalDate.parse(day).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),note="")
        val data = listOf(row(1,"2026-10-01",TransactionType.INCOME,120.25),row(2,"2026-10-02",TransactionType.EXPENSE,50.0),
            row(3,"2026-10-01",TransactionType.TRANSFER,90000.0),row(4,"2026-09-30",TransactionType.INCOME,30.0))
        val report = CalculateVisualReportUseCase()(data,LocalDate.parse("2026-10-01"),LocalDate.parse("2026-10-02"),ZoneOffset.UTC)
        assertEquals(2,report.transactionCount)
        assertEquals(java.math.BigDecimal("120.25"),report.buckets[0].income)
        assertEquals(java.math.BigDecimal("30.0"),report.buckets[1].previousIncome)
    }
    @Test fun emptyRangeHasZeroDataNotDemoBars() {
        val report = CalculateVisualReportUseCase()(emptyList(),LocalDate.parse("2026-10-01"),LocalDate.parse("2026-10-31"))
        assertEquals(0,report.transactionCount)
        assertTrue(report.buckets.all { it.income.signum() == 0 && it.expense.signum() == 0 })
    }
}

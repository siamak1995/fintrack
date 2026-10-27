package ir.siamak.fintrack.personalaccountant.domain
import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.domain.analytics.loanSchedule
import org.junit.Assert.*
import org.junit.Test
import java.time.*
class LoanScheduleTest {
    private val zone = ZoneId.of("Asia/Tehran")
    private fun loan(paid: Double = 0.0, total: Double = 1200000.0, count: Int = 12) = Installment(title = "وام",totalAmount = total,
        paidAmount = paid,dueDate = LocalDate.parse("2027-01-31").atStartOfDay(zone).toInstant().toEpochMilli(),createdAt = 0,note = null,walletId = 1,installmentCount = count)
    @Test fun countsRemainingAndFindsCalendarMonthEndWithoutDrift() {
        val result = loanSchedule(loan(200000.0),zone)
        assertEquals(2,result.paidCount); assertEquals(10,result.remainingCount)
        assertEquals(1000000.0,result.remainingAmount,0.0); assertEquals(LocalDate.parse("2027-03-31"),result.nextDue)
        assertEquals(LocalDate.parse("2027-12-31"),result.end)
    }
    @Test fun partialPaymentDoesNotClaimWholeInstallmentPaid() {
        val result = loanSchedule(loan(150000.0),zone)
        assertEquals(1,result.paidCount); assertEquals(50000.0,result.nextAmount,0.0)
        assertEquals(LocalDate.parse("2027-02-28"),result.nextDue)
    }
    @Test fun fullyPaidLoanHasNoNextDue() {
        val result = loanSchedule(loan(1200000.0),zone)
        assertEquals(0,result.remainingCount); assertNull(result.nextDue); assertEquals(0.0,result.remainingAmount,0.0)
    }
    @Test fun indivisibleTotalKeepsLastPaymentExact() {
        val result = loanSchedule(loan(666.67,total = 1000.0,count = 3),zone)
        assertEquals(2,result.paidCount); assertEquals(333.33,result.nextAmount,.00001)
        assertEquals(333.33,result.remainingAmount,.00001)
    }
}

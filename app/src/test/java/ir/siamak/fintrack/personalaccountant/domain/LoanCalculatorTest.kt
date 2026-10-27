package ir.siamak.fintrack.personalaccountant.domain

import ir.siamak.fintrack.personalaccountant.domain.analytics.*
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import java.math.BigDecimal
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class LoanCalculatorTest {
    @Test fun annualRateUsesMonthlyAmortization() {
        // Published PMT example: 10,000 at 8% annually, ten monthly instalments.
        val result = LoanCalculator.calculate(BigDecimal("10000"),BigDecimal("8"),10)
        assertEquals(BigDecimal("1037.03"),result.payment)
        assertEquals(result.total.subtract(BigDecimal("10000")),result.interest)
        assertTrue(result.total > BigDecimal("10000"))
    }
    @Test fun simpleAnnualInterestDependsOnPrincipalAndDuration() {
        val result = LoanCalculator.calculate(BigDecimal("100000"),BigDecimal("24"),10,LoanInterestMethod.SIMPLE)
        assertEquals(BigDecimal("12000.00"),result.payment)
        assertEquals(BigDecimal("120000.00"),result.total)
        assertEquals(BigDecimal("20000.00"),result.interest)
        assertTrue(result.total > LoanCalculator.calculate(BigDecimal("100000"),BigDecimal("24"),10).total)
    }
    @Test fun contractualAmountsAreNeverReplacedByCalculatedInterest() {
        assertTrue(runCatching { LoanCalculator.calculate(BigDecimal.TEN,BigDecimal.ONE,12,LoanInterestMethod.CONTRACT) }.isFailure)
    }
    @Test fun zeroRateDoesNotManufactureInterestFromRounding() {
        val result = LoanCalculator.calculate(BigDecimal("1000"),BigDecimal.ZERO,3)
        assertEquals(BigDecimal("333.33"),result.payment)
        assertEquals(0,result.total.compareTo(BigDecimal("1000")))
        assertEquals(0,result.interest.signum())
    }
    @Test fun invalidInputsNeverProduceNaNOrInfinitePayments() {
        listOf(-1,0,601).forEach { assertTrue(runCatching { LoanCalculator.calculate(BigDecimal.TEN,BigDecimal.ONE,it) }.isFailure) }
        assertTrue(runCatching { LoanCalculator.calculate(BigDecimal.ZERO,BigDecimal.ONE,12) }.isFailure)
        assertTrue(runCatching { LoanCalculator.calculate(BigDecimal.TEN,BigDecimal("101"),12) }.isFailure)
        assertTrue(runCatching { LoanCalculator.calculate(BigDecimal.TEN,BigDecimal("-1"),12) }.isFailure)
    }
    @Test fun verySmallRatesRemainStable() {
        val result = LoanCalculator.calculate(BigDecimal("100000000"),BigDecimal("0.000001"),600)
        assertTrue(result.payment.signum() > 0)
        assertTrue(result.total >= BigDecimal("100000000"))
    }
    @Test fun persianScheduleDoesNotDriftAfterAShortMonth() {
        val first = JalaliDateConverter.toGregorian(PersianDate(1405,6,31))
        assertEquals(PersianDate(1405,7,30),JalaliDateConverter.fromGregorian(loanDueDate(first,1)))
        assertEquals(PersianDate(1406,1,31),JalaliDateConverter.fromGregorian(loanDueDate(first,7)))
    }
    @Test fun currentMonthUsesPersianCalendarAcrossGregorianMonthBoundary() {
        assertTrue(samePersianMonth(LocalDate.parse("2026-09-25"),LocalDate.parse("2026-10-01")))
        assertFalse(samePersianMonth(LocalDate.parse("2026-10-20"),LocalDate.parse("2026-10-27")))
    }
}

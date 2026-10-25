package ir.siamak.fintrack.store.domain

import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.usecase.store.CalculateStoreReportUseCase
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class StoreReportsTest {
    private val calculator = CalculateStoreReportUseCase()
    private val filter = StoreReportFilter(from = LocalDate.parse("2026-10-01"), to = LocalDate.parse("2026-10-24"))
    private fun invoice(id: Long, date: String = "2026-10-20", paid: Long = 100, cancelled: Boolean = false) = Invoice(
        id = id, invoiceNumber = "S-$id", date = date, store = Store(name = "فروشگاه"),
        customer = Customer(id = id, name = "مشتری $id", phone = "0912$id"),
        items = listOf(SaleItem(10,"کالا",2,100), SaleItem(20,"دیگر",1,100)),
        discountAmount = 30, taxAmount = 27, finalAmount = 297,
        payment = Payment(Payment.PaymentType.CASH,paid), cancelled = cancelled,
        paymentStatus = if(paid == 297L) Invoice.PaymentStatus.SETTLED else Invoice.PaymentStatus.UNSETTLED
    )
    @Test fun invoiceWithManyItemsIsCountedOnceAndFinancialEquationIsExact() {
        val report = calculator(listOf(invoice(1),invoice(2,paid=297)),filter)
        assertEquals(2,report.totals.count); assertEquals(600,report.totals.grossItems)
        assertEquals(60,report.totals.discount); assertEquals(54,report.totals.tax)
        assertEquals(594,report.totals.payable); assertEquals(397,report.totals.received); assertEquals(197,report.totals.balance)
        assertEquals(0,report.totals.financeCharge)
    }
    @Test fun combinedCustomerProductSettlementAndDatesApplyTogether() {
        val result = calculator(listOf(invoice(1),invoice(2),invoice(1,date="2026-09-01")), filter.copy(customerId=1,productId=10,settlement=ReportSettlement.OUTSTANDING))
        assertEquals(1,result.totals.count); assertEquals(297,result.totals.payable)
        assertEquals(1,result.products.size); assertEquals(200,result.products.single().gross)
    }
    @Test fun cancelledOrdersAndSalesDoNotInflateTotalsAndModesDoNotMix() {
        val docs = listOf(invoice(1),invoice(2,cancelled=true),invoice(3).copy(preOrderId=3))
        val sales = calculator(docs,filter)
        assertEquals(1,sales.totals.count); assertEquals(1,sales.excludedCancelled)
        assertEquals(1,calculator(docs,filter.copy(document=ReportDocument.PREORDER)).totals.count)
    }
    @Test fun comparisonUsesSameDimensionsAndPriorPeriod() {
        val rows = listOf(invoice(1),invoice(1,date="2026-09-20"),invoice(2,date="2026-09-20"))
        val result = calculator(rows,filter.copy(customerId=1))
        assertEquals(LocalDate.parse("2026-09-07"),result.comparisonFrom)
        assertEquals(1,result.comparison.count)
    }
    @Test fun pagingChangesRowsNotTotalsAndClampsPage() {
        val report = calculator((1L..45L).map { invoice(it) },filter.copy(page=99,pageSize=20))
        assertEquals(45,report.totals.count); assertEquals(5,report.rows.size); assertEquals(45,report.allRows.size)
        assertEquals(2,report.filter.page); assertEquals(3,report.pages)
    }
    @Test fun searchNormalizesPersianDigitsWithoutChangingData() {
        val row = invoice(123)
        assertEquals(1,calculator(listOf(row),filter.copy(search="S-۱۲۳")).totals.count)
        assertEquals("S-123",row.invoiceNumber)
    }
    @Test fun financeAndScheduledDueAreNotConfusedWithCashFlow() {
        val invoice = invoice(1,paid=50).copy(finalAmount=330, payment=Payment(Payment.PaymentType.INSTALLMENT,50,
            listOf(Installment(1,5,330,"2026-10-01"))))
        val result = calculator(listOf(invoice),filter,LocalDate.parse("2026-10-24"))
        assertEquals(33,result.totals.financeCharge); assertEquals(1,result.totals.dueScheduleInvoices)
        assertEquals(280,result.totals.balance)
    }
    @Test(expected=IllegalArgumentException::class) fun reversedRangeIsRejected() {
        calculator(emptyList(),filter.copy(from=filter.to.plusDays(1)))
    }
    @Test fun invalidLegacyDatesAreReportedInsteadOfSilentlyDisappearing() {
        val result = calculator(listOf(invoice(1,date="unknown"),invoice(2)),filter)
        assertEquals(1,result.totals.count); assertEquals(1,result.invalidDates)
    }
    @Test fun legacyCashInvoiceWithoutItemsDoesNotInventInstallmentFinance() {
        val row = invoice(1,paid=0).copy(items=emptyList(),discountAmount=0,taxAmount=0,finalAmount=500)
        val result = calculator(listOf(row),filter)
        assertEquals(500,result.totals.payable); assertEquals(0,result.totals.financeCharge)
        assertEquals(1,result.totals.unreconciledInvoices); assertEquals(0,result.totals.grossItems)
    }
    @Test fun cashInvoiceDifferenceIsFlaggedWithoutChangingItsPersistedAmount() {
        val row = invoice(1).copy(finalAmount=330)
        val result = calculator(listOf(row),filter)
        assertEquals(330,result.totals.payable); assertEquals(0,result.totals.financeCharge)
        assertEquals(1,result.totals.unreconciledInvoices); assertEquals(330,row.finalAmount)
    }
}

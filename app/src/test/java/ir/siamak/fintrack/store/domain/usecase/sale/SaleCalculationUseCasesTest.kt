package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.Discount
import ir.siamak.fintrack.store.domain.model.SaleItem
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit coverage for sale total, fixed/percentage discount, and installment calculations. */
class SaleCalculationUseCasesTest {
    @Test fun calculatesSubtotal() = assertEquals(1_100_000, CalculateSaleAmountUseCase()(listOf(SaleItem(1, "کالا", 2, 550_000))))
    @Test fun appliesPercentDiscount() = assertEquals(100_000, ApplyDiscountUseCase()(1_000_000, Discount(Discount.DiscountType.PERCENT, 10)))
    @Test fun appliesFixedDiscount() = assertEquals(250_000, ApplyDiscountUseCase()(1_000_000, Discount(Discount.DiscountType.FIXED, 250_000)))
    @Test fun calculatesInstallments() { val installments = CalculateInstallmentUseCase()(10_000_000, 3, 5) { "1405/0$it/01" }; assertEquals(11_500_000, installments.sumOf { it.amount }) }
}

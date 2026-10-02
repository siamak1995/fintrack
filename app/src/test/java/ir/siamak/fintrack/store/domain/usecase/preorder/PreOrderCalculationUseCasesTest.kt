package ir.siamak.fintrack.store.domain.usecase.preorder

import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit coverage for pre-order suggested price, advance payment, and remaining balance. */
class PreOrderCalculationUseCasesTest {
    @Test fun calculatesSuggestedPrice() = assertEquals(1_600_000, CalculatePreOrderPriceUseCase()(500_000, 1_000_000, 0, 100_000))
    @Test fun calculatesAdvanceAndBalance() { val useCase = CalculateAdvancePaymentUseCase(); assertEquals(900_000, useCase.fromPercent(3_000_000, 30)); assertEquals(2_100_000, useCase.remaining(3_000_000, 900_000)) }
}

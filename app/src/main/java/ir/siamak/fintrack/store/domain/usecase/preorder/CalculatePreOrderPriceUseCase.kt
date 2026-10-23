package ir.siamak.fintrack.store.domain.usecase.preorder

/** Calculates a suggested custom-order price from its cost components. */
class CalculatePreOrderPriceUseCase {
    operator fun invoke(materialCost: Long, productionCost: Long, profit: Long, shippingCost: Long): Long {
        listOf(materialCost, productionCost, profit, shippingCost).forEach { require(it >= 0) { "Price component cannot be negative" } }
        return Math.addExact(Math.addExact(materialCost, productionCost), Math.addExact(profit, shippingCost))
    }
}

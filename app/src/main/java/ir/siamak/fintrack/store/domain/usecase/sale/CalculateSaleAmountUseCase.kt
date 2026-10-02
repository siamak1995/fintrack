package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.SaleItem

/** Calculates the pre-discount subtotal of selected sale items. */
class CalculateSaleAmountUseCase {
    operator fun invoke(items: List<SaleItem>): Long = items.sumOf { item ->
        require(item.quantity >= 0) { "Quantity cannot be negative" }
        require(item.unitPrice >= 0) { "Unit price cannot be negative" }
        item.quantity.toLong() * item.unitPrice
    }
}

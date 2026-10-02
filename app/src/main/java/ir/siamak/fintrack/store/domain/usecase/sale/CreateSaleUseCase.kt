package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.Discount
import ir.siamak.fintrack.store.domain.model.Payment
import ir.siamak.fintrack.store.domain.model.Sale
import ir.siamak.fintrack.store.domain.model.SaleItem
import ir.siamak.fintrack.store.domain.model.Tax
import ir.siamak.fintrack.store.domain.repository.SaleRepository

/** Creates a sale only after all monetary values have been calculated in domain code. */
class CreateSaleUseCase(
    private val repository: SaleRepository,
    private val calculateSaleAmount: CalculateSaleAmountUseCase,
    private val applyDiscount: ApplyDiscountUseCase
) {
    suspend operator fun invoke(customerId: Long, sellerId: Long?, items: List<SaleItem>, discount: Discount?, tax: Tax, payment: Payment, createdAt: String): Long {
        require(customerId > 0) { "Customer is required" }
        require(tax.percent in 0..MAX_TAX_PERCENT) { "Tax percentage is invalid" }
        val subtotal = calculateSaleAmount(items)
        val discountAmount = applyDiscount(subtotal, discount)
        val taxableAmount = subtotal - discountAmount
        val taxAmount = taxableAmount * tax.percent / PERCENT_BASE
        val finalAmount = taxableAmount + taxAmount
        return repository.create(Sale(0, customerId, sellerId, items, discount, tax, payment, subtotal, discountAmount, taxAmount, finalAmount, createdAt))
    }

    private companion object { const val PERCENT_BASE = 100L; const val MAX_TAX_PERCENT = 100 }
}

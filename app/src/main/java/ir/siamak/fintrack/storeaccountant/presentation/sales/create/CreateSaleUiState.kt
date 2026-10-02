package ir.siamak.fintrack.storeaccountant.presentation.sales.create

import ir.siamak.fintrack.storeaccountant.domain.model.Customer
import ir.siamak.fintrack.storeaccountant.domain.model.Product
import ir.siamak.fintrack.storeaccountant.domain.model.SaleItem

data class CreateSaleUiState(
    val currentStep: CreateSaleStep = CreateSaleStep.Customer,
    val selectedCustomer: Customer? = null,
    val selectedProducts: Map<Product, Int> = emptyList<Pair<Product, Int>>().toMap(), // Mapping Product to Quantity
    val discountValue: String = "",
    val discountType: String = "FIXED", // PERCENT, FIXED
    val taxPercent: String = "0",
    val isLoading: Boolean = false,
    val isFinished: Boolean = false
)

enum class CreateSaleStep {
    Customer, Products, Discount, Payment, Invoice
}

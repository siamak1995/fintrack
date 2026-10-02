package ir.siamak.fintrack.storeaccountant.domain.model

data class Sale(
    val id: Long = 0,
    val customer: Customer,
    val items: List<SaleItem>,
    val discount: Discount?,
    val tax: Tax?,
    val date: String,
    val totalAmount: Long,
    val finalAmount: Long
)

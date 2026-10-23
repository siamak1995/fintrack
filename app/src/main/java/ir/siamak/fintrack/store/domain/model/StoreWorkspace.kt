package ir.siamak.fintrack.store.domain.model

/** Immutable offline workspace assembled from persistent store data. */
data class StoreWorkspace(
    val store: Store? = null,
    val sellers: List<Seller> = emptyList(),
    val products: List<Product> = emptyList(),
    val materials: List<RawMaterial> = emptyList(),
    val sales: List<Sale> = emptyList(),
    val orders: List<PreOrder> = emptyList(),
    val invoices: List<Invoice> = emptyList()
)

/** Validated sale request; product prices are resolved from the persisted catalog. */
data class SaleRequest(
    val customer: Customer,
    val sellerId: Long?,
    val quantities: Map<Long, Int>,
    val discount: Discount? = null,
    val taxPercent: Int = 0,
    val installmentMonths: Int = 0,
    val monthlyPercent: Int = 0,
    val firstDueDate: String = "",
    val requestId: String
)

/** Custom order request; material costs are resolved from inventory. */
data class PreOrderRequest(
    val customer: Customer,
    val description: String,
    val deliveryDate: String,
    val quantities: Map<Long, Double>,
    val productionCost: Long,
    val profit: Long,
    val shippingCost: Long,
    val agreedPrice: Long,
    val advance: Long,
    val advanceIsPercent: Boolean,
    val requestId: String
)

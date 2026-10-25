package ir.siamak.fintrack.store.domain.usecase.store
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.repository.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/** Combines live store flows without retaining a second mutable data source. */
class ObserveStoreWorkspaceUseCase @Inject constructor(
    private val stores: StoreRepository,
    private val sellers: SellerRepository,
    private val products: ProductRepository,
    private val materials: MaterialRepository,
    private val sales: SaleRepository,
    private val orders: PreOrderRepository,
    private val invoices: InvoiceRepository
) {
    operator fun invoke(): Flow<StoreWorkspace> {
        val profiles = combine(stores.observeStores(), stores.observeProfiles()) { all, preferences -> all to preferences }
        val catalog = combine(profiles, sellers.observeAll(), products.observeAll(), materials.observeAll()) {
            (all, preferences), people, goods, stock -> StoreWorkspace(
                all.firstOrNull { it.id == preferences.activeStoreId } ?: all.firstOrNull(), people, goods, stock,
                stores = all, brands = preferences.brands)
        }
        return combine(catalog, sales.observeAll(), orders.observeAll(), invoices.observeAll()) {
            workspace, receipts, custom, documents ->
            val scoped = documents.forStore(workspace.store, workspace.stores)
            val saleIds = scoped.mapNotNull { it.saleId }.toSet()
            val orderIds = scoped.mapNotNull { it.preOrderId }.toSet()
            val legacyStore = workspace.store == null || workspace.store.id == workspace.stores.firstOrNull()?.id
            val allSaleIds = documents.mapNotNull { it.saleId }.toSet()
            val allOrderIds = documents.mapNotNull { it.preOrderId }.toSet()
            workspace.copy(sales = receipts.filter { it.id in saleIds || (legacyStore && it.id !in allSaleIds) },
                orders = custom.filter { it.id in orderIds || (legacyStore && it.id !in allOrderIds) }, invoices = scoped)
        }
    }
}

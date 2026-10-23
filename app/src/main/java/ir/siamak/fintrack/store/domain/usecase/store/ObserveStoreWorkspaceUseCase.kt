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
        val catalog = combine(stores.observeStore(), sellers.observeAll(), products.observeAll(), materials.observeAll()) {
            store, people, goods, stock -> StoreWorkspace(store, people, goods, stock)
        }
        return combine(catalog, sales.observeAll(), orders.observeAll(), invoices.observeAll()) {
            workspace, receipts, custom, documents -> workspace.copy(sales = receipts, orders = custom, invoices = documents)
        }
    }
}

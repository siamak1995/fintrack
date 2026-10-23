package ir.siamak.fintrack.store.domain.usecase.sale
import ir.siamak.fintrack.store.domain.model.*
import javax.inject.Inject
/** Public sale workflow: never persists a header without inventory, payment and invoice. */
class CreateSaleUseCase @Inject constructor(private val checkout: CheckoutSaleUseCase) {
    fun preview(request: SaleRequest, workspace: StoreWorkspace) = checkout.preview(request,workspace)
    suspend operator fun invoke(request: SaleRequest, workspace: StoreWorkspace) = checkout(request,workspace)
}

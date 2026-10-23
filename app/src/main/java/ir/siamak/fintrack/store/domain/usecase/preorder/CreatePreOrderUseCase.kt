package ir.siamak.fintrack.store.domain.usecase.preorder
import ir.siamak.fintrack.store.domain.model.*
import javax.inject.Inject
class CreatePreOrderUseCase @Inject constructor(private val place: PlacePreOrderUseCase) {
    fun preview(request: PreOrderRequest, workspace: StoreWorkspace) = place.preview(request,workspace)
    fun materialCost(request: PreOrderRequest, workspace: StoreWorkspace) = place.materialCost(request,workspace)
    fun suggested(request: PreOrderRequest, workspace: StoreWorkspace) = place.suggested(request,workspace)
    suspend operator fun invoke(request: PreOrderRequest, workspace: StoreWorkspace) = place(request,workspace)
}

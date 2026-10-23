package ir.siamak.fintrack.store.presentation.preorder.create
import ir.siamak.fintrack.store.domain.model.*
data class CreatePreOrderUiState(val draft: PreOrderDraft = PreOrderDraft(), val workspace: StoreWorkspace = StoreWorkspace(),
    val loading: Boolean = true, val busy: Boolean = false, val preview: PreOrder? = null, val suggested: Long? = null, val materialCost: Long? = null,
    val error: String? = null, val invoiceId: Long? = null)

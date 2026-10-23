package ir.siamak.fintrack.store.presentation.sales.create
import ir.siamak.fintrack.store.domain.model.*
data class CreateSaleUiState(val draft: SaleDraft = SaleDraft(), val workspace: StoreWorkspace = StoreWorkspace(),
    val loading: Boolean = true, val busy: Boolean = false, val preview: Sale? = null, val error: String? = null, val invoiceId: Long? = null)

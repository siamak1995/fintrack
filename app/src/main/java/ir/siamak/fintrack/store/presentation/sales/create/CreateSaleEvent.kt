package ir.siamak.fintrack.store.presentation.sales.create
sealed interface CreateSaleEvent {
    data class Edit(val change: (SaleDraft) -> SaleDraft) : CreateSaleEvent
    data object Next : CreateSaleEvent
    data object Back : CreateSaleEvent
    data object Submit : CreateSaleEvent
}

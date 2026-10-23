package ir.siamak.fintrack.store.presentation.preorder.create
sealed interface CreatePreOrderEvent {
    data class Edit(val change: (PreOrderDraft) -> PreOrderDraft) : CreatePreOrderEvent
    data object Next : CreatePreOrderEvent
    data object Back : CreatePreOrderEvent
    data object Submit : CreatePreOrderEvent
}

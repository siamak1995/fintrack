package ir.siamak.fintrack.storeaccountant.presentation.sales.list

sealed class SalesEvent {
    object Refresh : SalesEvent()
}

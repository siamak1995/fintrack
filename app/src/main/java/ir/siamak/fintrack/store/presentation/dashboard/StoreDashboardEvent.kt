package ir.siamak.fintrack.store.presentation.dashboard

/** User intents exposed by the store dashboard. */
sealed interface StoreDashboardEvent { data object CreateSale : StoreDashboardEvent; data object CreatePreOrder : StoreDashboardEvent }

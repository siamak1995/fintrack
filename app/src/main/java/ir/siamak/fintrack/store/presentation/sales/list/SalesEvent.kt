package ir.siamak.fintrack.store.presentation.sales.list

/** User intents from the sales overview. */
sealed interface SalesEvent { data object CreateSale : SalesEvent; data object OpenInvoices : SalesEvent }

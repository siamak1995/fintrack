package ir.siamak.fintrack.account.presentation.session

/** User intents handled by the application-shell session state. */
sealed interface AppSessionEvent {
    data class SwitchContext(val contextId: Long) : AppSessionEvent
    data object ClearError : AppSessionEvent
}

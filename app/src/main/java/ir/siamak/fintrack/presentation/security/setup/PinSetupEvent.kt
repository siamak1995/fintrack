package ir.siamak.fintrack.presentation.security.setup

sealed interface PinSetupEvent {
    data class DigitClicked(val digit: Int) : PinSetupEvent
    data object BackspaceClicked : PinSetupEvent
    data object Dismiss : PinSetupEvent
}

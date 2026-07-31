package ir.siamak.fintrack.presentation.security.lock

/**
 * User events for the application lock screen.
 */
sealed interface LockEvent {

    data class DigitClicked(val digit: Int) : LockEvent

    data object BackspaceClicked : LockEvent

    data object ClearClicked : LockEvent

    data object BiometricClicked : LockEvent

    data object BiometricSucceeded : LockEvent

    data class BiometricFailed(val message: String) : LockEvent
}

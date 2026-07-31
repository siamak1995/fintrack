package ir.siamak.fintrack.presentation.security.lock

/**
 * UI state for the application lock screen.
 */
data class LockState(
    val enteredPin: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUnlocked: Boolean = false,
    val canUseBiometric: Boolean = false
)

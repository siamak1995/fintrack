package ir.siamak.fintrack.presentation.security.lock

/**
 * UI state for the application lock screen.
 */
data class LockState(
    val isLoading: Boolean = false,
    val enteredPin: String = "",
    val errorMessage: String? = null,
    val isUnlocked: Boolean = false,
    val shouldShowLockScreen: Boolean = false,
    val canUseBiometric: Boolean = false
)

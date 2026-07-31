package ir.siamak.fintrack.domain.security.model

/**
 * Represents local security preferences used for application lock behavior.
 */
data class SecuritySettings(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val hasPin: Boolean = false
) {
    val shouldShowLockScreen: Boolean
        get() = isPinEnabled && hasPin

    val canUseBiometric: Boolean
        get() = isPinEnabled && isBiometricEnabled && hasPin
}

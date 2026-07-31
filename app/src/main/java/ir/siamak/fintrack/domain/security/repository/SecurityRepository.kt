package ir.siamak.fintrack.domain.security.repository

import ir.siamak.fintrack.domain.security.model.SecuritySettings
import kotlinx.coroutines.flow.Flow

/**
 * Provides security settings and PIN verification operations.
 */
interface SecurityRepository {

    fun observeSecuritySettings(): Flow<SecuritySettings>

    suspend fun setPin(pin: String)

    suspend fun verifyPin(pin: String): Boolean

    suspend fun clearPin()

    suspend fun setBiometricEnabled(enabled: Boolean)
}

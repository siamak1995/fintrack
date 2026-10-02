package ir.siamak.fintrack.account.presentation.session

import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.common.core.error.AppError

/** Immutable state exposed by the application shell for context-aware navigation. */
data class AppSessionState(
    val userId: Long? = null,
    val isAuthenticated: Boolean = false,
    val activeContext: AccountantContext? = null,
    val availableContexts: List<AccountantContext> = emptyList(),
    val isLoading: Boolean = true,
    val error: AppError? = null
)

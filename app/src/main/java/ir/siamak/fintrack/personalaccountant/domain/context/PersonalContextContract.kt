package ir.siamak.fintrack.personalaccountant.domain.context

import ir.siamak.fintrack.common.core.error.AppError

/** Compatibility value for legacy personal flows until they are session-bound. */
const val LEGACY_PERSONAL_CONTEXT_ID = 1L

/** A context id must be explicit and positive at every personal-domain boundary. */
fun requireValidPersonalContextId(contextId: Long) {
    require(contextId > 0L) { AppError.InvalidContext.toString() }
}

/** Prevents a command from being applied to an entity owned by another context. */
fun requireContextOwnership(requestContextId: Long, entityContextId: Long) {
    requireValidPersonalContextId(requestContextId)
    require(entityContextId == requestContextId) { AppError.UnauthorizedContextAccess.toString() }
}

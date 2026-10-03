package ir.siamak.fintrack.personalaccountant.data.model

import ir.siamak.fintrack.personalaccountant.domain.context.LEGACY_PERSONAL_CONTEXT_ID

data class Tag(
    val id: Long = 0L,
    val contextId: Long = LEGACY_PERSONAL_CONTEXT_ID,
    val name: String,
    val color: Long? = null,
    val workspaceId: Long? = null,
    val allowedType: TransactionType = TransactionType.EXPENSE
)


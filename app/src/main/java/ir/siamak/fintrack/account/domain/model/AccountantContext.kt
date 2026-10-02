package ir.siamak.fintrack.account.domain.model

/** Identifies the accounting workspace currently available to a user. */
data class AccountantContext(
    val id: Long,
    val ownerUserId: Long,
    val type: AccountantType,
    val name: String,
    val description: String? = null,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/** Supported accounting workspace types. */
enum class AccountantType { PERSONAL, STORE }

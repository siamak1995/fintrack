package ir.siamak.fintrack.personalaccountant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Durable registry for accounting contexts. DataStore persists only the active
 * selection; this table owns the contexts that may be selected.
 */
@Entity(tableName = "accountant_contexts")
data class AccountantContextEntity(
    @PrimaryKey
    val id: Long,
    val ownerUserId: Long,
    val type: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

object AccountantContextIds {
    const val PERSONAL: Long = 1L
    const val STORE: Long = 2L
}

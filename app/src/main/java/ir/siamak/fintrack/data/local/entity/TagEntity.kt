package ir.siamak.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.siamak.fintrack.data.model.TransactionType

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val color: Long? = null,
    val workspaceId: Long? = null,
    val allowedType: TransactionType
)

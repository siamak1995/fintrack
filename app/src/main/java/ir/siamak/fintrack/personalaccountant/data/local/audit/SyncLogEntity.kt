package ir.siamak.fintrack.personalaccountant.data.local.audit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_logs")
data class SyncLogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val entityName: String,

    val entityId: Long,

    val action: SyncAction,

    val createdAt: Long = System.currentTimeMillis(),

    val synced: Boolean = false,

    val retryCount: Int = 0,

    val errorMessage: String? = null
)

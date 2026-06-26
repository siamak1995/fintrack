package ir.siamak.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val relation: String,
    val color: String,
    val icon: String
)

package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room representation of a seller. */
@Entity(tableName = "sellers") data class SellerEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val firstName: String, val lastName: String, val phone: String, val address: String?, val description: String?)

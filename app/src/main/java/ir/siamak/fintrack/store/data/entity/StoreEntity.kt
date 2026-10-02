package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room representation of a store profile. */
@Entity(tableName = "stores") data class StoreEntity(@PrimaryKey val id: Long, val name: String, val brand: String, val logoPath: String?, val phone: String?, val address: String?, val description: String?)

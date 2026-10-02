package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room representation of a customer. */
@Entity(tableName = "customers") data class CustomerEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val phone: String, val address: String?)

package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room representation of a sellable product. */
@Entity(tableName = "products") data class ProductEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val material: String?, val size: String?, val weight: String?, val stock: Int, val price: Long, val image: String?)

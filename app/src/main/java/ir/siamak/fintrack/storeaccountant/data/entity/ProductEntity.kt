package ir.siamak.fintrack.storeaccountant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * انتیتی مربوط به کالاها در دیتابیس.
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val material: String?,
    val size: String?,
    val weight: String?,
    val stock: Int,
    val price: Long,
    val image: String?
)

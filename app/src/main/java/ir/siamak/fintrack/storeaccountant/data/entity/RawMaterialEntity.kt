package ir.siamak.fintrack.storeaccountant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * انتیتی مربوط به مواد اولیه در دیتابیس.
 */
@Entity(tableName = "raw_materials")
data class RawMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val unit: String,
    val pricePerUnit: Long,
    val quantity: Double,
    val purchaseDate: String?,
    val shippingCost: Long?
)

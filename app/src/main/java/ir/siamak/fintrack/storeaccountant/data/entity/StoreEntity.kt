package ir.siamak.fintrack.storeaccountant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * انتیتی مربوط به اطلاعات فروشگاه در دیتابیس.
 */
@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val logoPath: String?,
    val phone: String?,
    val address: String?,
    val description: String?
)


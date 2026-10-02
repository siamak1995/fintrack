package ir.siamak.fintrack.storeaccountant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * انتیتی مربوط به فروشندگان در دیتابیس.
 */
@Entity(tableName = "sellers")
data class SellerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: String?,
    val description: String?
)

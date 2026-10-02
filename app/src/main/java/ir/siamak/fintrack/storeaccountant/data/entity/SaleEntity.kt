package ir.siamak.fintrack.storeaccountant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val date: String,
    val totalAmount: Long,
    val discountValue: Long,
    val discountType: String?, // PERCENT, FIXED
    val taxPercent: Int,
    val finalAmount: Long,
    val walletId: Long? // Added for integration with personal accountant wallets
)

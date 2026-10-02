package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted line item linked to a sale. */
@Entity(tableName = "sale_items") data class SaleItemEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val saleId: Long, val productId: Long, val productName: String, val quantity: Int, val unitPrice: Long)

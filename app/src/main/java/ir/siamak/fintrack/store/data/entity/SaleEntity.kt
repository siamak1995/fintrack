package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted monetary totals for a completed sale. */
@Entity(tableName = "sales") data class SaleEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val customerId: Long, val sellerId: Long?, val subtotal: Long, val discountAmount: Long, val taxAmount: Long, val finalAmount: Long, val paymentType: String, val createdAt: String)

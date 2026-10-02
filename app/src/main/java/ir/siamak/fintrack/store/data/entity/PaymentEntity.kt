package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted payment state for a sale or pre-order. */
@Entity(tableName = "payments") data class PaymentEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val saleId: Long?, val preOrderId: Long?, val type: String, val paidAmount: Long)

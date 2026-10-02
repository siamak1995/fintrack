package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted raw-material requirement of a pre-order. */
@Entity(tableName = "pre_order_items") data class PreOrderItemEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val preOrderId: Long, val rawMaterialId: Long, val materialName: String, val quantity: Double, val unitPrice: Long)

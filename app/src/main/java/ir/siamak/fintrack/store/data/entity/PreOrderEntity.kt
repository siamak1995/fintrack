package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted custom pre-order. */
@Entity(tableName = "pre_orders") data class PreOrderEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val customerId: Long, val description: String, val deliveryDate: String, val agreedPrice: Long, val shippingCost: Long, val status: String, val advancePayment: Long)

package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted invoice index record. */
@Entity(tableName = "invoices", indices = [androidx.room.Index(value = ["invoiceNumber"], unique = true)]) data class InvoiceEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val invoiceNumber: String, val saleId: Long?, val preOrderId: Long?, val date: String, val finalAmount: Long, val paymentStatus: String, @androidx.room.ColumnInfo(defaultValue = "NONE") val payload: String = "NONE")

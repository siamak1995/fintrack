package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted invoice index record. */
@Entity(tableName = "invoices") data class InvoiceEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val invoiceNumber: String, val saleId: Long?, val preOrderId: Long?, val date: String, val finalAmount: Long, val paymentStatus: String)

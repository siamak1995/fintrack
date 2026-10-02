package ir.siamak.fintrack.store.data.mapper

import ir.siamak.fintrack.store.data.entity.InvoiceEntity

/** Maps the persistable invoice index fields. */
data class InvoiceIndex(val id: Long, val invoiceNumber: String, val date: String, val finalAmount: Long, val paymentStatus: String)
fun InvoiceEntity.toIndex() = InvoiceIndex(id, invoiceNumber, date, finalAmount, paymentStatus)

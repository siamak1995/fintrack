package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

/** Invoice data boundary. */
interface InvoiceRepository { fun observeAll(): Flow<List<Invoice>>; suspend fun save(invoice: Invoice) }

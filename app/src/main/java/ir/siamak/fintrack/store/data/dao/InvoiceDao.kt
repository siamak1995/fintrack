package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for invoice indices. */
@Dao interface InvoiceDao { @Query("SELECT * FROM invoices ORDER BY date DESC") fun observeAll(): Flow<List<InvoiceEntity>>; @Upsert suspend fun upsert(entity: InvoiceEntity) }

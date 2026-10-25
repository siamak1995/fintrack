package ir.siamak.fintrack.store.data.dao
import androidx.room.*
import ir.siamak.fintrack.store.data.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow
@Dao interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY date DESC, id DESC") fun observeAll(): Flow<List<InvoiceEntity>>
    @Query("SELECT * FROM invoices") suspend fun records(): List<InvoiceEntity>
    @Query("SELECT * FROM invoices WHERE id = :id") suspend fun get(id: Long): InvoiceEntity?
    @Query("SELECT * FROM invoices WHERE invoiceNumber = :number") suspend fun byNumber(number: String): InvoiceEntity?
    @Query("SELECT * FROM invoices WHERE preOrderId = :id") suspend fun forOrder(id: Long): List<InvoiceEntity>
    @Upsert suspend fun upsert(entity: InvoiceEntity): Long
}

package ir.siamak.fintrack.store.data.dao
import androidx.room.*
import ir.siamak.fintrack.store.data.entity.*
import kotlinx.coroutines.flow.Flow

data class SaleRecord(
    @Embedded val sale: SaleEntity,
    @Relation(parentColumn = "id", entityColumn = "saleId") val items: List<SaleItemEntity>,
    @Relation(entity = PaymentEntity::class, parentColumn = "id", entityColumn = "saleId") val payments: List<PaymentRecord>,
    @Relation(parentColumn = "id", entityColumn = "saleId") val invoices: List<InvoiceEntity>
)
data class PaymentRecord(
    @Embedded val payment: PaymentEntity,
    @Relation(parentColumn = "id", entityColumn = "paymentId") val installments: List<InstallmentEntity>
)
@Dao interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC, id DESC") fun observeAll(): Flow<List<SaleEntity>>
    @Transaction @Query("SELECT * FROM sales ORDER BY createdAt DESC, id DESC") fun observeRecords(): Flow<List<SaleRecord>>
    @Insert suspend fun insert(entity: SaleEntity): Long
    @Insert suspend fun insertItems(items: List<SaleItemEntity>)
    @Insert suspend fun insertPayment(payment: PaymentEntity): Long
    @Insert suspend fun insertInstallments(items: List<InstallmentEntity>)
    @Query("SELECT * FROM payments WHERE saleId = :saleId LIMIT 1") suspend fun paymentForSale(saleId: Long): PaymentEntity?
    @Query("SELECT * FROM payments WHERE preOrderId = :orderId LIMIT 1") suspend fun paymentForOrder(orderId: Long): PaymentEntity?
    @Query("UPDATE payments SET paidAmount = :amount WHERE id = :id") suspend fun updatePaid(id: Long, amount: Long)
}

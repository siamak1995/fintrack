package ir.siamak.fintrack.store.data.dao
import androidx.room.*
import ir.siamak.fintrack.store.data.entity.*
import kotlinx.coroutines.flow.Flow
data class PreOrderRecord(
    @Embedded val order: PreOrderEntity,
    @Relation(parentColumn = "id", entityColumn = "preOrderId") val items: List<PreOrderItemEntity>
)
@Dao interface PreOrderDao {
    @Query("SELECT * FROM pre_orders ORDER BY deliveryDate") fun observeAll(): Flow<List<PreOrderEntity>>
    @Transaction @Query("SELECT * FROM pre_orders ORDER BY deliveryDate, id DESC") fun observeRecords(): Flow<List<PreOrderRecord>>
    @Insert suspend fun insert(entity: PreOrderEntity): Long
    @Update suspend fun update(entity: PreOrderEntity)
    @Insert suspend fun insertItems(items: List<PreOrderItemEntity>)
    @Transaction @Query("SELECT * FROM pre_orders WHERE id = :id") suspend fun record(id: Long): PreOrderRecord?
    @Query("UPDATE pre_orders SET status = :status WHERE id = :id") suspend fun status(id: Long, status: String)
    @Query("UPDATE pre_orders SET advancePayment = :amount WHERE id = :id") suspend fun updatePaid(id: Long, amount: Long)
}

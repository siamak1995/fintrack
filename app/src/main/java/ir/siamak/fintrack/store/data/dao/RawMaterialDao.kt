package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.RawMaterialEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for raw materials. */
@Dao interface RawMaterialDao { @Query("SELECT * FROM raw_materials ORDER BY name") fun observeAll(): Flow<List<RawMaterialEntity>>; @Upsert suspend fun upsert(entity: RawMaterialEntity); @Query("DELETE FROM raw_materials WHERE id = :id AND NOT EXISTS (SELECT 1 FROM pre_order_items i INNER JOIN pre_orders o ON o.id = i.preOrderId WHERE i.rawMaterialId = :id AND o.status NOT IN ('CANCELLED','DELIVERED'))") suspend fun delete(id: Long): Int @Query("SELECT * FROM raw_materials WHERE id = :id") suspend fun get(id: Long): RawMaterialEntity?; @Query("UPDATE raw_materials SET quantity = MAX(0, quantity - :amount) WHERE id = :id AND quantity + 0.000000001 >= :amount AND pricePerUnit = :price") suspend fun consume(id: Long, amount: Double, price: Long): Int; @Query("UPDATE raw_materials SET quantity = quantity + :amount WHERE id = :id") suspend fun restore(id: Long, amount: Double) }

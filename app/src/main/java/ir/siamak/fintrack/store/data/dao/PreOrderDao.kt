package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.store.data.entity.PreOrderEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for custom pre-orders. */
@Dao interface PreOrderDao { @Query("SELECT * FROM pre_orders ORDER BY deliveryDate") fun observeAll(): Flow<List<PreOrderEntity>>; @Insert suspend fun insert(entity: PreOrderEntity): Long; @Update suspend fun update(entity: PreOrderEntity) }

package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for the single store profile. */
@Dao interface StoreDao {
    @Query("SELECT * FROM stores ORDER BY id LIMIT 1") fun observe(): Flow<StoreEntity?>
    @Query("SELECT * FROM stores ORDER BY id") fun observeAll(): Flow<List<StoreEntity>>
    @Query("SELECT * FROM stores ORDER BY id") suspend fun records(): List<StoreEntity>
    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM stores") suspend fun nextId(): Long
    @Query("DELETE FROM stores WHERE id = :id") suspend fun delete(id: Long)
    @Upsert suspend fun upsert(entity: StoreEntity)
}

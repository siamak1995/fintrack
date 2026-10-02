package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for the single store profile. */
@Dao interface StoreDao { @Query("SELECT * FROM stores LIMIT 1") fun observe(): Flow<StoreEntity?>; @Upsert suspend fun upsert(entity: StoreEntity) }

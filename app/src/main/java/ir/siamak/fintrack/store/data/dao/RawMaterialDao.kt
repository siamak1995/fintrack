package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.RawMaterialEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for raw materials. */
@Dao interface RawMaterialDao { @Query("SELECT * FROM raw_materials ORDER BY name") fun observeAll(): Flow<List<RawMaterialEntity>>; @Upsert suspend fun upsert(entity: RawMaterialEntity); @Query("DELETE FROM raw_materials WHERE id = :id") suspend fun delete(id: Long) }

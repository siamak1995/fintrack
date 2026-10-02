package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.SellerEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for sellers. */
@Dao interface SellerDao { @Query("SELECT * FROM sellers ORDER BY firstName") fun observeAll(): Flow<List<SellerEntity>>; @Upsert suspend fun upsert(entity: SellerEntity); @Query("DELETE FROM sellers WHERE id = :id") suspend fun delete(id: Long) }

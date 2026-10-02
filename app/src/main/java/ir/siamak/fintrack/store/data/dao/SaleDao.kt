package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.siamak.fintrack.store.data.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for sales. */
@Dao interface SaleDao { @Query("SELECT * FROM sales ORDER BY createdAt DESC") fun observeAll(): Flow<List<SaleEntity>>; @Insert suspend fun insert(entity: SaleEntity): Long }

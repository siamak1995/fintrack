package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.CustomerEntity

/** Data access operations for sale and pre-order customers. */
@Dao interface CustomerDao { @Upsert suspend fun upsert(entity: CustomerEntity): Long; @Query("SELECT * FROM customers WHERE id = :id") suspend fun find(id: Long): CustomerEntity? }

package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for products. */
@Dao interface ProductDao { @Query("SELECT * FROM products ORDER BY name") fun observeAll(): Flow<List<ProductEntity>>; @Upsert suspend fun upsert(entity: ProductEntity); @Query("DELETE FROM products WHERE id = :id") suspend fun delete(id: Long) @Query("SELECT * FROM products WHERE id = :id") suspend fun get(id: Long): ProductEntity?; @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity AND price = :price") suspend fun consume(id: Long, quantity: Int, price: Long): Int }

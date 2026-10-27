package ir.siamak.fintrack.store.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.siamak.fintrack.store.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/** Data access operations for products. */
@Dao interface ProductDao {
    @Query("SELECT * FROM products") suspend fun records(): List<ProductEntity>
    @androidx.room.Transaction suspend fun saveUnique(entity: ProductEntity) {
        val key = ir.siamak.fintrack.store.domain.model.productNameKey(entity.name)
        require(key.isNotBlank()) { "نام کالا الزامی است" }
        require(records().none { it.id != entity.id && ir.siamak.fintrack.store.domain.model.productNameKey(it.name) == key }) { "کالایی با این نام وجود دارد؛ همان کالا را انتخاب یا ویرایش کنید" }
        upsert(entity.copy(name = entity.name.trim()))
    }
 @Query("SELECT * FROM products ORDER BY name") fun observeAll(): Flow<List<ProductEntity>>; @Upsert suspend fun upsert(entity: ProductEntity); @Query("DELETE FROM products WHERE id = :id") suspend fun delete(id: Long) @Query("SELECT * FROM products WHERE id = :id") suspend fun get(id: Long): ProductEntity?; @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity AND price = :price") suspend fun consume(id: Long, quantity: Int, price: Long): Int }

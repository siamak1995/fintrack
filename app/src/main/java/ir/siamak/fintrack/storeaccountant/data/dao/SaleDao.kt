package ir.siamak.fintrack.storeaccountant.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ir.siamak.fintrack.storeaccountant.data.entity.SaleEntity
import ir.siamak.fintrack.storeaccountant.data.entity.SaleItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY id DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Transaction
    suspend fun createSale(sale: SaleEntity, items: List<SaleItemEntity>) {
        val saleId = insertSale(sale)
        val itemsWithId = items.map { it.copy(saleId = saleId) }
        insertSaleItems(itemsWithId)
    }
}

package ir.siamak.fintrack.storeaccountant.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.storeaccountant.data.entity.SellerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SellerDao {
    @Query("SELECT * FROM sellers ORDER BY lastName ASC")
    fun getAllSellers(): Flow<List<SellerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeller(seller: SellerEntity)

    @Update
    suspend fun updateSeller(seller: SellerEntity)

    @Delete
    suspend fun deleteSeller(seller: SellerEntity)
}

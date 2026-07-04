package ir.siamak.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Delete
    suspend fun deleteWallet(wallet: WalletEntity)

    @Query("SELECT * FROM wallet ORDER BY id DESC")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE id = :walletId LIMIT 1")
    suspend fun getWalletById(walletId: Long): WalletEntity?

}
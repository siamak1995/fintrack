package ir.siamak.fintrack.personalaccountant.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.personalaccountant.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE contextId = 1 AND isDeleted = 0 AND isCash = 0")
    suspend fun smsBankWallets(): List<WalletEntity>

    @Query("SELECT * FROM wallet WHERE contextId = 1 AND isCash = 1 LIMIT 1")
    suspend fun cashWallet(): WalletEntity?

    @Query("SELECT * FROM wallet WHERE contextId = 1 AND isDeleted = 0 AND name = 'پول نقد' ORDER BY id LIMIT 1")
    suspend fun legacyCashWallet(): WalletEntity?

    @androidx.room.Transaction
    suspend fun ensureCash() {
        val existing = cashWallet()
        if(existing != null) {
            if(existing.isDeleted || existing.name != "پول نقد") updateWallet(existing.copy(isDeleted = false, name = "پول نقد"))
        } else {
            val legacy = legacyCashWallet()
            if(legacy != null) updateWallet(legacy.copy(isCash = true))
            else insertWallet(WalletEntity(name = "پول نقد", balance = 0.0, color = "#008577", isCash = true))
        }
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @androidx.room.Transaction
    suspend fun deleteWallet(wallet: WalletEntity) {
        val existing = requireNotNull(getWalletById(wallet.id))
        require(!existing.isCash) { "حساب پول نقد قابل حذف نیست" }
        updateWallet(existing.copy(isDeleted = true, updatedAt = System.currentTimeMillis()))
    }

    @Query("SELECT * FROM wallet WHERE isDeleted = 0 ORDER BY isCash DESC, id DESC")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE id = :walletId LIMIT 1")
    suspend fun getWalletById(walletId: Long): WalletEntity?

}

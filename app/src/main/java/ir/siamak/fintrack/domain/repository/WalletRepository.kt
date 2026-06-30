package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Wallet // فرض می‌کنیم مدل‌ها در این مسیر هستند
import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    // گرفتن تمام کیف پول‌ها به صورت Flow برای آپدیت‌های ریل تایم
    fun getAllWallets(): Flow<List<Wallet>>

    // گرفتن یک کیف پول بر اساس شناسه
    suspend fun getWalletById(id: Long): Wallet?

    suspend fun insert(wallet: Wallet)

    suspend fun update(wallet: Wallet)

    suspend fun delete(wallet: Wallet)

    fun getAll(): Flow<List<Wallet>>

    suspend fun getById(id: Long): Wallet?
}

package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Wallet // فرض می‌کنیم مدل‌ها در این مسیر هستند
import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    // گرفتن تمام کیف پول‌ها به صورت Flow برای آپدیت‌های ریل تایم
    fun getAllWallets(): Flow<List<Wallet>>

    // گرفتن یک کیف پول بر اساس شناسه
    suspend fun getWalletById(id: Long): Wallet?

    // اضافه یا به‌روزرسانی کردن یک کیف پول
    suspend fun insertWallet(wallet: Wallet)

    // حذف یک کیف پول
    suspend fun deleteWallet(wallet: Wallet)
}

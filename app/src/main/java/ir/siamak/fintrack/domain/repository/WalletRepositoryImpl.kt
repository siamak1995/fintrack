package ir.siamak.fintrack.data.repository

import ir.siamak.fintrack.data.local.dao.FinTrackDao
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject // برای تزریق DAO توسط Hilt

// تزریق DAO به Repository توسط Hilt
class WalletRepositoryImpl @Inject constructor(
    private val dao: FinTrackDao
) : WalletRepository {

    override fun getAllWallets(): Flow<List<Wallet>> {
        return dao.getAllWallets() // متد موجود در FinTrackDao
    }

    override suspend fun getWalletById(id: Long): Wallet? {
        return dao.getWalletById(id)
    }

    override suspend fun insertWallet(wallet: Wallet) {
        dao.insertWallet(wallet)
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        dao.deleteWallet(wallet)
    }
}

package ir.siamak.fintrack.personalaccountant.data.repository

import ir.siamak.fintrack.personalaccountant.data.local.dao.WalletDao
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val dao: WalletDao
) : WalletRepository {

    override fun getAllWallets(): Flow<List<Wallet>> {
        // تبدیل لیست Entity به لیست Model با استفاده از map و مپر
        return dao.getAllWallets().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getWalletById(id: Long): Wallet? {
        // تبدیل تک Entity به Model
        return dao.getWalletById(id)?.toModel()
    }

    override suspend fun insert(wallet: Wallet) {
        dao.insertWallet(wallet.toEntity())
    }

    override suspend fun update(wallet: Wallet) {
        dao.updateWallet(wallet.toEntity())
    }

    override suspend fun delete(wallet: Wallet) {
        dao.deleteWallet(wallet.toEntity())
    }

    override fun getAll(): Flow<List<Wallet>> {
        return getAllWallets()
    }

    override suspend fun getById(id: Long): Wallet? {
        return getWalletById(id)
    }
}


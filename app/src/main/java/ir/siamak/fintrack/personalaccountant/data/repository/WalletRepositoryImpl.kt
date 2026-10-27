package ir.siamak.fintrack.personalaccountant.data.repository

import ir.siamak.fintrack.personalaccountant.data.local.dao.WalletDao
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val dao: WalletDao
) : WalletRepository {

    override fun getAllWallets(): Flow<List<Wallet>> {
        // تبدیل لیست Entity به لیست Model با استفاده از map و مپر
        return dao.getAllWallets().onStart { dao.ensureCash() }.map { entities ->
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
        val existing = requireNotNull(dao.getWalletById(wallet.id))
        dao.updateWallet(existing.copy(name = if(existing.isCash) "پول نقد" else wallet.name,
            balance = wallet.balance, color = wallet.color,
            cardNumber = if(existing.isCash) null else wallet.cardNumber,
            accountNumber = if(existing.isCash) null else wallet.accountNumber, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun delete(wallet: Wallet) {
        val existing = requireNotNull(dao.getWalletById(wallet.id))
        require(!existing.isCash) { "حساب پیش‌فرض پول نقد قابل حذف نیست" }
        // Archive rather than CASCADE-delete the financial history.
        dao.updateWallet(existing.copy(isDeleted = true, updatedAt = System.currentTimeMillis()))
    }

    override fun getAll(): Flow<List<Wallet>> {
        return getAllWallets()
    }

    override suspend fun getById(id: Long): Wallet? {
        return getWalletById(id)
    }
}


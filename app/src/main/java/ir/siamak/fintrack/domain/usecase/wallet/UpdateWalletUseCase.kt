package ir.siamak.fintrack.domain.usecase.wallet

import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.repository.WalletRepository
import javax.inject.Inject

class UpdateWalletUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        repository.updateWallet(wallet)
    }
}

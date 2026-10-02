package ir.siamak.fintrack.personalaccountant.domain.usecase.wallet

import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
import javax.inject.Inject

class UpdateWalletUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        repository.update(wallet)
    }
}


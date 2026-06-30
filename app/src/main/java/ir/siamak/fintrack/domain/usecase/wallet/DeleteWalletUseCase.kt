package ir.siamak.fintrack.domain.usecase.wallet

import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * یوزکیس مربوط به حذف کیف پول.
 */
class DeleteWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    /**
     * اجرای یوزکیس برای حذف یک کیف پول.
     *
     * @param wallet کیف پولی که باید حذف شود
     */
    suspend operator fun invoke(wallet: Wallet) {
        walletRepository.delete(wallet)
    }
}

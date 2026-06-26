package ir.siamak.fintrack.domain.usecase.wallet

import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * یوزکیس مربوط به دریافت یک کیف پول بر اساس شناسه.
 */
class GetWalletByIdUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    /**
     * اجرای یوزکیس برای دریافت یک کیف پول خاص.
     *
     * @param id شناسه کیف پول
     * @return شیء کیف پول در صورت وجود، وگرنه null
     */
    suspend operator fun invoke(id: Long): Wallet? {
        return walletRepository.getWalletById(id)
    }
}

package ir.siamak.fintrack.personalaccountant.domain.usecase.wallet

import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
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


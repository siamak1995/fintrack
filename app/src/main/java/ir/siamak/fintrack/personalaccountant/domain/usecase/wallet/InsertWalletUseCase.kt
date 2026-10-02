package ir.siamak.fintrack.personalaccountant.domain.usecase.wallet

import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * یوزکیس مربوط به افزودن یا ذخیره کیف پول.
 */
class InsertWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    /**
     * اجرای یوزکیس برای ذخیره کیف پول.
     *
     * @param wallet اطلاعات کیف پول برای ذخیره در دیتابیس
     */
    suspend operator fun invoke(wallet: Wallet) {
        walletRepository.insert(wallet)
    }
}


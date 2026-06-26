package ir.siamak.fintrack.domain.usecase.wallet

import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * یوزکیس مربوط به دریافت لیست تمام کیف پول‌ها.
 *
 * این کلاس به ViewModel کمک می‌کند بدون وابستگی مستقیم
 * به Repository، داده‌های مورد نیاز خود را دریافت کند.
 */
class GetAllWalletsUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    /**
     * اجرای یوزکیس برای دریافت همه کیف پول‌ها به صورت Flow.
     *
     * @return لیست کیف پول‌ها که با هر تغییر در دیتابیس آپدیت می‌شود
     */
    operator fun invoke(): Flow<List<Wallet>> {
        return walletRepository.getAllWallets()
    }
}

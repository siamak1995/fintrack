package ir.siamak.fintrack.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.domain.usecase.wallet.DeleteWalletUseCase
import ir.siamak.fintrack.domain.usecase.wallet.GetAllWalletsUseCase
import ir.siamak.fintrack.domain.usecase.wallet.GetWalletByIdUseCase
import ir.siamak.fintrack.domain.usecase.wallet.InsertWalletUseCase
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases

/**
 * ماژول تزریق وابستگی مربوط به UseCaseهای برنامه.
 *
 * در این ماژول، UseCaseهای مرتبط با Wallet در قالب یک کلاس تجمیعی
 * به نام [WalletUseCases] فراهم می‌شوند تا در ViewModelها راحت‌تر
 * تزریق و استفاده شوند.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * فراهم کردن مجموعه UseCaseهای مرتبط با Wallet.
     *
     * @return نمونه‌ای از [WalletUseCases]
     */
    @Provides
    fun provideWalletUseCases(
        getAllWalletsUseCase: GetAllWalletsUseCase,
        getWalletByIdUseCase: GetWalletByIdUseCase,
        insertWalletUseCase: InsertWalletUseCase,
        deleteWalletUseCase: DeleteWalletUseCase
    ): WalletUseCases {
        return WalletUseCases(
            getAllWallets = getAllWalletsUseCase,
            getWalletById = getWalletByIdUseCase,
            insertWallet = insertWalletUseCase,
            deleteWallet = deleteWalletUseCase
        )
    }
}

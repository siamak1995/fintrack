package ir.siamak.fintrack.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.data.repository.WalletRepositoryImpl
import ir.siamak.fintrack.domain.repository.WalletRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Hilt بداند که برای WalletRepository باید از WalletRepositoryImpl استفاده کند
    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: WalletRepositoryImpl
    ): WalletRepository
}

package ir.siamak.fintrack.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.data.repository.WalletRepositoryImpl
import ir.siamak.fintrack.domain.repository.WalletRepository
import ir.siamak.fintrack.data.repository.TransactionRepositoryImpl
import ir.siamak.fintrack.domain.repository.TransactionRepository
import javax.inject.Singleton

/**
 * ماژول مربوط به اتصال Interfaceها به Implementationها در لایه Repository.
 *
 * وظیفه این ماژول:
 * - مشخص کردن این که هر زمان [WalletRepository] درخواست شد،
 *   Hilt باید نمونه‌ای از [WalletRepositoryImpl] را تزریق کند.
 *
 * این الگو باعث می‌شود:
 * - لایه Domain فقط Interface را بشناسد
 * - لایه Data پیاده‌سازی واقعی را نگه دارد
 * - پروژه تست‌پذیرتر و قابل نگهداری‌تر شود
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * اتصال پیاده‌سازی [WalletRepositoryImpl] به اینترفیس [WalletRepository].
     *
     * @param walletRepositoryImpl پیاده‌سازی واقعی ریپازیتوری کیف پول
     * @return نمونه‌ای از [WalletRepository]
     */
    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: WalletRepositoryImpl
    ): WalletRepository

    @Binds
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository
}

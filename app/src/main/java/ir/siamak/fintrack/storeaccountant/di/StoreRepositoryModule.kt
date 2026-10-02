package ir.siamak.fintrack.storeaccountant.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.storeaccountant.data.repository.MaterialRepositoryImpl
import ir.siamak.fintrack.storeaccountant.data.repository.ProductRepositoryImpl
import ir.siamak.fintrack.storeaccountant.data.repository.SellerRepositoryImpl
import ir.siamak.fintrack.storeaccountant.data.repository.StoreRepositoryImpl
import ir.siamak.fintrack.storeaccountant.domain.repository.MaterialRepository
import ir.siamak.fintrack.storeaccountant.domain.repository.ProductRepository
import ir.siamak.fintrack.storeaccountant.domain.repository.SellerRepository
import ir.siamak.fintrack.storeaccountant.domain.repository.StoreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository

    @Binds
    @Singleton
    abstract fun bindMaterialRepository(impl: MaterialRepositoryImpl): MaterialRepository

    @Binds
    @Singleton
    abstract fun bindSellerRepository(impl: SellerRepositoryImpl): SellerRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
}

package ir.siamak.fintrack.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.account.data.repository.DataStoreContextRepository
import ir.siamak.fintrack.account.domain.repository.ContextRepository
import javax.inject.Singleton

/** Hilt bindings for application-shell context dependencies. */
@Module
@InstallIn(SingletonComponent::class)
abstract class ContextModule {
    @Binds
    @Singleton
    abstract fun bindContextRepository(implementation: DataStoreContextRepository): ContextRepository
}

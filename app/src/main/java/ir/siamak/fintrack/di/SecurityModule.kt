package ir.siamak.fintrack.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.data.security.PinHasher
import ir.siamak.fintrack.data.security.SecurityRepositoryImpl
import ir.siamak.fintrack.data.security.Sha256PinHasher
import ir.siamak.fintrack.domain.security.repository.SecurityRepository
import javax.inject.Singleton

/**
 * Hilt module for security related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun providePinHasher(): PinHasher = Sha256PinHasher()

    @Provides
    @Singleton
    fun provideSecurityRepository(
        dataStore: DataStore<Preferences>,
        pinHasher: PinHasher
    ): SecurityRepository = SecurityRepositoryImpl(dataStore, pinHasher)
}

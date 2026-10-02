package ir.siamak.fintrack.storeaccountant.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.storeaccountant.data.dao.ProductDao
import ir.siamak.fintrack.storeaccountant.data.dao.RawMaterialDao
import ir.siamak.fintrack.storeaccountant.data.dao.SellerDao
import ir.siamak.fintrack.storeaccountant.data.dao.StoreDao
import ir.siamak.fintrack.storeaccountant.data.local.StoreDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoreDatabaseModule {

    @Provides
    @Singleton
    fun provideStoreDatabase(
        @ApplicationContext context: Context
    ): StoreDatabase {
        return Room.databaseBuilder(
            context,
            StoreDatabase::class.java,
            "store_fintrack_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStoreDao(database: StoreDatabase): StoreDao {
        return database.storeDao()
    }

    @Provides
    @Singleton
    fun provideRawMaterialDao(database: StoreDatabase): RawMaterialDao {
        return database.rawMaterialDao()
    }

    @Provides
    @Singleton
    fun provideSellerDao(database: StoreDatabase): SellerDao {
        return database.sellerDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: StoreDatabase): ProductDao {
        return database.productDao()
    }
}


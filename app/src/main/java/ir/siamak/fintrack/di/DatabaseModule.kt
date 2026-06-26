package ir.siamak.fintrack.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.data.local.dao.FinTrackDao
import ir.siamak.fintrack.data.local.database.AppDatabase // مسیر جدید بر اساس ساختار شما
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // مسیر جدید: data.local.database
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fintrack_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFinTrackDao(database: AppDatabase): FinTrackDao {
        return database.finTrackDao()
    }
}

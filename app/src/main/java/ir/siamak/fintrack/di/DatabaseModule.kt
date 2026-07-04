package ir.siamak.fintrack.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.data.local.dao.FinTrackDao
import ir.siamak.fintrack.data.local.dao.InstallmentDao
import ir.siamak.fintrack.data.local.dao.MemberDao
import ir.siamak.fintrack.data.local.database.AppDatabase
import javax.inject.Singleton

/**
 * ماژول تزریق وابستگی مربوط به دیتابیس برنامه.
 *
 * وظیفه این ماژول:
 * - ساخت نمونه Singleton از [AppDatabase]
 * - فراهم کردن نمونه [FinTrackDao] برای استفاده در Repositoryها
 *
 * این ماژول در سطح [SingletonComponent] نصب می‌شود، بنابراین
 * در کل طول عمر برنامه فقط یک نمونه از دیتابیس و DAO ساخته خواهد شد.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // اضافه کردن ستون جدید به جدول تراکنش‌ها
            db.execSQL("ALTER TABLE transactions ADD COLUMN toWalletId INTEGER DEFAULT NULL")
            // ساخت ایندکس برای ستون جدید جهت بهبود پرفورمنس
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_toWalletId ON transactions(toWalletId)")
        }
    }

    /**
     * ساخت و ارائه نمونه Singleton از دیتابیس اصلی برنامه با استفاده از Room.
     *
     * @param context کانتکست اپلیکیشن که توسط Hilt تزریق می‌شود.
     * @return نمونه ساخته‌شده از [AppDatabase]
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fintrack_db"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    /**
     * ارائه DAO اصلی برنامه از روی نمونه دیتابیس.
     *
     * @param database نمونه دیتابیس برنامه
     * @return نمونه [FinTrackDao] برای انجام عملیات CRUD
     */
    @Provides
    @Singleton
    fun provideFinTrackDao(database: AppDatabase): FinTrackDao = database.finTrackDao()

    /**
     * ارائه DAO اصلی برنامه از روی نمونه دیتابیس.
     *
     * @param database نمونه دیتابیس برنامه
     * @return نمونه [MemberDao] برای انجام عملیات CRUD
     */
    @Provides
    @Singleton
    fun provideMemberDao(database: AppDatabase): MemberDao = database.memberDao()

    /**
     * ارائه DAO اصلی برنامه از روی نمونه دیتابیس.
     *
     * @param database نمونه دیتابیس برنامه
     * @return نمونه [InstallmentDao] برای انجام عملیات CRUD
     */
    @Provides
    @Singleton
    fun provideInstallmentDao(database: AppDatabase): InstallmentDao = database.installmentDao()






}

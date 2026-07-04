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
import ir.siamak.fintrack.data.local.dao.InstallmentDao
import ir.siamak.fintrack.data.local.dao.MemberDao
import ir.siamak.fintrack.data.local.dao.TagDao
import ir.siamak.fintrack.data.local.dao.TransactionDao
import ir.siamak.fintrack.data.local.dao.WalletDao
import ir.siamak.fintrack.data.local.database.AppDatabase
import javax.inject.Singleton

/**
 * ماژول تزریق وابستگی مربوط به دیتابیس برنامه.
 *
 * وظیفه این ماژول:
 * - ساخت نمونه Singleton از [AppDatabase]
 *
 * این ماژول در سطح [SingletonComponent] نصب می‌شود، بنابراین
 * در کل طول عمر برنامه فقط یک نمونه از دیتابیس و DAO ساخته خواهد شد.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN toWalletId INTEGER DEFAULT NULL")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_toWalletId ON transactions(toWalletId)")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS tags (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    color INTEGER,
                    workspaceId INTEGER
                )
            """.trimIndent())

            database.execSQL("""
                CREATE TABLE IF NOT EXISTS transaction_tags (
                    transactionId INTEGER NOT NULL,
                    tagId INTEGER NOT NULL,
                    PRIMARY KEY(transactionId, tagId),
                    FOREIGN KEY(transactionId) REFERENCES transactions(id) ON DELETE CASCADE,
                    FOREIGN KEY(tagId) REFERENCES tags(id) ON DELETE CASCADE
                )
            """.trimIndent())

            database.execSQL("CREATE INDEX IF NOT EXISTS index_transaction_tags_transactionId ON transaction_tags(transactionId)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transaction_tags_tagId ON transaction_tags(tagId)")
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
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }

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

    @Provides
    @Singleton
    fun provideWalletDao(
        database: AppDatabase
    ): WalletDao = database.walletDao()


    @Provides
    @Singleton
    fun provideTransactionDao(
        database: AppDatabase
    ): TransactionDao = database.transactionDao()

    @Provides
    @Singleton
    fun provideTagDao(
        database: AppDatabase
    ): TagDao = database.tagDao()
}

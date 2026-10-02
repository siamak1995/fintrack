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
import ir.siamak.fintrack.personalaccountant.data.local.dao.InstallmentDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.MemberDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.TagDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.TransactionDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.WalletDao
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    /**
     * Migration 1 -> 2
     * اضافه شدن کیف پول مقصد برای انتقال بین حساب‌ها
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {

            if (!columnExists(
                    db,
                    "transactions",
                    "toWalletId"
                )
            ) {
                db.execSQL(
                    """
                    ALTER TABLE transactions 
                    ADD COLUMN toWalletId INTEGER
                    """.trimIndent()
                )
            }

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS 
                index_transactions_toWalletId 
                ON transactions(toWalletId)
                """.trimIndent()
            )
        }
    }


    /**
     * Migration 2 -> 3
     * اضافه شدن Tag
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS tags(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    color INTEGER,
                    workspaceId INTEGER
                )
                """.trimIndent()
            )


            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS transaction_tags(
                    transactionId INTEGER NOT NULL,
                    tagId INTEGER NOT NULL,

                    PRIMARY KEY(transactionId,tagId),

                    FOREIGN KEY(transactionId)
                    REFERENCES transactions(id)
                    ON DELETE CASCADE,

                    FOREIGN KEY(tagId)
                    REFERENCES tags(id)
                    ON DELETE CASCADE
                )
                """.trimIndent()
            )


            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS 
                index_transaction_tags_transactionId
                ON transaction_tags(transactionId)
                """.trimIndent()
            )


            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS 
                index_transaction_tags_tagId
                ON transaction_tags(tagId)
                """.trimIndent()
            )
        }
    }



    /**
     * Migration 3 -> 4
     * اضافه شدن Relation ها و فیلدهای تکمیلی
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {

            // رزرو شده برای تغییرات ساختاری آینده
        }
    }



    /**
     * Migration 4 -> 5
     * اضافه شدن Audit Field
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {


            addAuditColumns(
                db,
                "transactions"
            )


            addAuditColumns(
                db,
                "tags"
            )


            addAuditColumns(
                db,
                "wallet"
            )


            addAuditColumns(
                db,
                "members"
            )


            addAuditColumns(
                db,
                "installment"
            )
        }
    }



    /**
     * Migration 5 -> 6
     * اضافه شدن Sync
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {

        override fun migrate(db: SupportSQLiteDatabase) {


            addColumnIfMissing(
                db,
                "transactions",
                "syncState",
                "TEXT NOT NULL DEFAULT 'LOCAL_ONLY'"
            )


            addColumnIfMissing(
                db,
                "transactions",
                "serverId",
                "INTEGER"
            )


            addColumnIfMissing(
                db,
                "tags",
                "syncState",
                "TEXT NOT NULL DEFAULT 'LOCAL_ONLY'"
            )


            addColumnIfMissing(
                db,
                "tags",
                "serverId",
                "INTEGER"
            )


            addColumnIfMissing(
                db,
                "wallet",
                "syncState",
                "TEXT NOT NULL DEFAULT 'LOCAL_ONLY'"
            )


            addColumnIfMissing(
                db,
                "wallet",
                "serverId",
                "INTEGER"
            )

        }
    }



    /**
     * Migration 6 -> 7
     * نسخه فعلی Sync + Audit
     */
    val MIGRATION_6_7 = object : Migration(6,7){

        override fun migrate(db: SupportSQLiteDatabase) {


            addColumnIfMissing(
                db,
                "installment",
                "syncState",
                "TEXT NOT NULL DEFAULT 'LOCAL_ONLY'"
            )


            addColumnIfMissing(
                db,
                "installment",
                "serverId",
                "INTEGER"
            )


        }
    }

    /**
     * Migration 7 -> 8
     * Adds the durable context registry and assigns every legacy personal
     * record to the default Personal context. Foreign keys are intentionally
     * deferred to a later table-rebuild migration.
     */
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS accountant_contexts (
                    id INTEGER NOT NULL,
                    ownerUserId INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    isActive INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT OR IGNORE INTO accountant_contexts
                    (id, ownerUserId, type, name, description, isActive, createdAt, updatedAt)
                VALUES (1, 0, 'PERSONAL', 'Personal', NULL, 1, 0, 0)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO accountant_contexts
                    (id, ownerUserId, type, name, description, isActive, createdAt, updatedAt)
                VALUES (2, 0, 'STORE', 'Store', NULL, 1, 0, 0)
                """.trimIndent()
            )

            listOf("wallet", "transactions", "members", "tags", "installment").forEach { table ->
                addColumnIfMissing(db, table, "contextId", "INTEGER NOT NULL DEFAULT 1")
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_${table}_contextId ON $table(contextId)"
                )
            }
        }
    }



    @Provides
    @Singleton
    @PersonalAccountingDatabase
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {


        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fintrack_db"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8
            )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    }





    @Provides
    @Singleton
    fun provideMemberDao(
        @PersonalAccountingDatabase
        database: AppDatabase
    ): MemberDao =
        database.memberDao()



    @Provides
    @Singleton
    fun provideInstallmentDao(
        @PersonalAccountingDatabase
        database: AppDatabase
    ): InstallmentDao =
        database.installmentDao()



    @Provides
    @Singleton
    fun provideWalletDao(
        @PersonalAccountingDatabase
        database: AppDatabase
    ): WalletDao =
        database.walletDao()



    @Provides
    @Singleton
    fun provideTransactionDao(
        @PersonalAccountingDatabase
        database: AppDatabase
    ): TransactionDao =
        database.transactionDao()



    @Provides
    @Singleton
    fun provideTagDao(
        @PersonalAccountingDatabase
        database: AppDatabase
    ): TagDao =
        database.tagDao()



    private fun addAuditColumns(
        db: SupportSQLiteDatabase,
        table:String
    ){

        addColumnIfMissing(
            db,
            table,
            "createdAt",
            "INTEGER NOT NULL DEFAULT 0"
        )


        addColumnIfMissing(
            db,
            table,
            "updatedAt",
            "INTEGER NOT NULL DEFAULT 0"
        )


        addColumnIfMissing(
            db,
            table,
            "isDeleted",
            "INTEGER NOT NULL DEFAULT 0"
        )


        addColumnIfMissing(
            db,
            table,
            "version",
            "INTEGER NOT NULL DEFAULT 1"
        )

    }




    private fun addColumnIfMissing(
        db:SupportSQLiteDatabase,
        table:String,
        column:String,
        definition:String
    ){

        if(!columnExists(db,table,column)){

            db.execSQL(
                """
                ALTER TABLE $table
                ADD COLUMN $column $definition
                """.trimIndent()
            )
        }

    }





    private fun columnExists(
        db:SupportSQLiteDatabase,
        table:String,
        column:String
    ):Boolean{


        val cursor =
            db.query("PRAGMA table_info($table)")


        while(cursor.moveToNext()){


            val name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                )


            if(name == column){

                cursor.close()
                return true
            }

        }


        cursor.close()

        return false
    }

}

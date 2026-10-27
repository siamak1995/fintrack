package ir.siamak.fintrack.personalaccountant.data.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ir.siamak.fintrack.di.DatabaseModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Verifies that personal-database migrations preserve legacy data. */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    @Test fun migrates9To10WithoutReinterpretingOrDeletingFinancialHistory() {
        val name = "personal-v10-preservation"
        helper.createDatabase(name,9).use { db ->
            db.execSQL("INSERT INTO wallet(id,name,balance,color,createdAt,updatedAt,isDeleted,version,syncState,serverId,contextId,isCash) VALUES(1,'Bank',123456.7,'#008577',12,12,0,1,'LOCAL_ONLY',NULL,1,0)")
            db.execSQL("INSERT INTO members(id,name,relation,color,icon,createdAt,updatedAt,isDeleted,version,syncState,contextId) VALUES(1,'Person','self','#000000','person',12,12,0,1,'LOCAL_ONLY',1)")
            db.execSQL("INSERT INTO installment(id,title,totalAmount,paidAmount,dueDate,note,walletId,isPaid,createdAt,updatedAt,isDeleted,version,syncState,contextId,installmentCount,startDate,automaticPayment) VALUES(1,'Contract',12345.67,2345.67,1793000000000,'keep',1,0,123,123,0,1,'LOCAL_ONLY',1,12,123,1)")
            db.execSQL("INSERT INTO transactions(id,amount,type,categoryName,walletId,memberId,date,note,createdAt,updatedAt,isDeleted,version,syncState,contextId) VALUES(1,2345.67,'EXPENSE','Legacy',1,1,1793000000000,'keep receipt',123,123,0,1,'LOCAL_ONLY',1)")
            db.execSQL("INSERT INTO tags(id,name,allowedType,createdAt,updatedAt,isDeleted,version,syncState,contextId) VALUES(1,'قسط','EXPENSE',123,123,0,1,'LOCAL_ONLY',1)")
            db.execSQL("INSERT INTO transaction_tags(transactionId,tagId) VALUES(1,1)")
        }
        helper.runMigrationsAndValidate(name,10,true,DatabaseModule.MIGRATION_9_10).use { db ->
            assertEquals(123456.7,db.query("SELECT balance FROM wallet WHERE id=1").singleDouble(),0.0)
            assertEquals(12345.67,db.query("SELECT totalAmount FROM installment WHERE id=1").singleDouble(),0.0)
            assertEquals(2345.67,db.query("SELECT paidAmount FROM installment WHERE id=1").singleDouble(),0.0)
            assertEquals(2345.67,db.query("SELECT openingPaidAmount FROM installment WHERE id=1").singleDouble(),0.0)
            assertEquals(1L,db.query("SELECT COUNT(*) FROM installment WHERE principalAmount IS NULL AND annualInterestRate IS NULL AND installmentCount=12 AND automaticPayment=1 AND note='keep'").singleLong())
            assertEquals(1L,db.query("SELECT COUNT(*) FROM transactions WHERE loanId IS NULL AND note='keep receipt'").singleLong())
            assertEquals(1L,db.query("SELECT COUNT(*) FROM transaction_tags").singleLong())
        }
    }
    @Test fun complete6To10MigrationChainRetainsOriginalLoan() {
        val name = "personal-v6-v10-preservation"
        helper.createDatabase(name,6).use { db ->
            db.execSQL("INSERT INTO installment(id,title,totalAmount,paidAmount,dueDate,note,walletId,isPaid,createdAt,updatedAt,isDeleted,version,syncState,serverId) VALUES(1,'Old loan',1000000,100000,1790000000000,'original',1,0,123,123,0,1,'LOCAL_ONLY',NULL)")
        }
        helper.runMigrationsAndValidate(name,10,true,DatabaseModule.MIGRATION_6_7,DatabaseModule.MIGRATION_7_8,DatabaseModule.MIGRATION_8_9,DatabaseModule.MIGRATION_9_10).use { db ->
            assertEquals(1L,db.query("SELECT COUNT(*) FROM installment WHERE totalAmount=1000000 AND paidAmount=100000 AND openingPaidAmount=100000 AND startDate=123 AND contextId=1 AND note='original'").singleLong())
        }
    }
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName
    )

    @Test fun migratesVersion8To9PreservingBalancesAndLegacyLoanDates() {
        val name = "personal-v9-migration"
        helper.createDatabase(name,8).use { db ->
            db.execSQL("INSERT INTO wallet(id,name,balance,color,createdAt,updatedAt,isDeleted,version,syncState,serverId,contextId) VALUES(1,'Bank',123456,'#008577',12,12,0,1,'LOCAL_ONLY',NULL,1)")
            db.execSQL("INSERT INTO installment(id,title,totalAmount,paidAmount,dueDate,note,walletId,isPaid,createdAt,updatedAt,isDeleted,version,syncState,serverId,contextId) VALUES(1,'Loan',100000,20000,1793000000000,NULL,1,0,123,123,0,1,'LOCAL_ONLY',NULL,1)")
        }
        helper.runMigrationsAndValidate(name,9,true,DatabaseModule.MIGRATION_8_9).use { db ->
            assertEquals(123456L,db.query("SELECT CAST(balance AS INTEGER) FROM wallet WHERE id=1").singleLong())
            assertEquals(123L,db.query("SELECT startDate FROM installment WHERE id=1").singleLong())
            assertEquals(1L,db.query("SELECT installmentCount FROM installment WHERE id=1").singleLong())
            assertEquals(0L,db.query("SELECT automaticPayment FROM installment WHERE id=1").singleLong())
        }
    }

    @Test
    fun migratesVersion6ToVersion7WithoutLosingInstallments() {
        helper.createDatabase(TEST_DATABASE_NAME, VERSION_6).use { database ->
            database.execSQL(
                "INSERT INTO installment (id, title, totalAmount, paidAmount, dueDate, note, walletId, isPaid, createdAt, updatedAt, isDeleted, version, syncState, serverId) VALUES (1, 'قسط آزمایشی', 1000000, 100000, 1790000000000, NULL, 1, 0, 0, 0, 0, 1, 'LOCAL_ONLY', NULL)"
            )
        }

        helper.runMigrationsAndValidate(TEST_DATABASE_NAME, VERSION_7, true, DatabaseModule.MIGRATION_6_7).use { database ->
            assertEquals(1L, database.query("SELECT COUNT(*) FROM installment").singleLong())
            assertTrue(database.columnExists("installment", "syncState"))
            assertTrue(database.columnExists("installment", "serverId"))
        }
    }

    @Test
    fun migratesVersion7ToVersion8AndAssignsLegacyDataToPersonalContext() {
        helper.createDatabase(TEST_DATABASE_NAME, VERSION_7).use { database ->
            database.execSQL(
                "INSERT INTO wallet (id, name, balance, color, createdAt, updatedAt, isDeleted, version, syncState, serverId) VALUES (1, 'Legacy wallet', 250000, '#000000', 0, 0, 0, 1, 'LOCAL_ONLY', NULL)"
            )
            database.execSQL(
                "INSERT INTO members (id, name, relation, color, icon, createdAt, updatedAt, isDeleted, version, syncState, serverId) VALUES (1, 'Legacy member', 'friend', '#000000', 'person', 0, 0, 0, 1, 'LOCAL_ONLY', NULL)"
            )
            database.execSQL(
                "INSERT INTO tags (id, name, color, workspaceId, allowedType, createdAt, updatedAt, isDeleted, version, syncState, serverId) VALUES (1, 'Legacy tag', NULL, NULL, 'EXPENSE', 0, 0, 0, 1, 'LOCAL_ONLY', NULL)"
            )
            database.execSQL(
                "INSERT INTO transactions (id, amount, type, categoryName, walletId, toWalletId, memberId, date, note, createdAt, updatedAt, isDeleted, version, syncState, serverId) VALUES (1, 250000, 'EXPENSE', 'Food', 1, NULL, 1, 1790000000000, 'Legacy transaction', 0, 0, 0, 1, 'LOCAL_ONLY', NULL)"
            )
            database.execSQL(
                "INSERT INTO installment (id, title, totalAmount, paidAmount, dueDate, note, walletId, isPaid, createdAt, updatedAt, isDeleted, version, syncState, serverId) VALUES (1, 'Legacy installment', 1000000, 100000, 1790000000000, NULL, 1, 0, 0, 0, 0, 1, 'LOCAL_ONLY', NULL)"
            )
        }

        helper.runMigrationsAndValidate(TEST_DATABASE_NAME, VERSION_8, true, DatabaseModule.MIGRATION_7_8).use { database ->
            assertEquals(2L, database.query("SELECT COUNT(*) FROM accountant_contexts").singleLong())
            assertEquals(1L, database.query("SELECT COUNT(*) FROM accountant_contexts WHERE id = 1 AND type = 'PERSONAL'").singleLong())
            listOf("wallet", "transactions", "members", "tags", "installment").forEach { table ->
                assertTrue(database.columnExists(table, "contextId"))
                assertEquals(1L, database.query("SELECT contextId FROM $table WHERE id = 1").singleLong())
                assertTrue(database.indexExists("index_${table}_contextId"))
            }
            assertEquals(250000.0, database.query("SELECT amount FROM transactions WHERE id = 1").singleDouble(), 0.0)
        }
    }

    @Test
    fun migratesEmptyVersion7DatabaseToVersion8() {
        helper.createDatabase(TEST_DATABASE_NAME, VERSION_7).close()

        helper.runMigrationsAndValidate(TEST_DATABASE_NAME, VERSION_8, true, DatabaseModule.MIGRATION_7_8).use { database ->
            assertEquals(2L, database.query("SELECT COUNT(*) FROM accountant_contexts").singleLong())
            assertEquals(0L, database.query("SELECT COUNT(*) FROM wallet").singleLong())
        }
    }

    private fun SupportSQLiteDatabase.columnExists(table: String, column: String): Boolean =
        query("PRAGMA table_info($table)").use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow("name")) == column) return true
            }
            false
        }

    private fun SupportSQLiteDatabase.indexExists(index: String): Boolean =
        query("SELECT 1 FROM sqlite_master WHERE type = 'index' AND name = '$index'").use { cursor ->
            cursor.moveToFirst()
        }

    private fun android.database.Cursor.singleLong(): Long = use { cursor ->
        check(cursor.moveToFirst()) { "Query did not return a row" }
        cursor.getLong(0)
    }

    private fun android.database.Cursor.singleDouble(): Double = use { cursor ->
        check(cursor.moveToFirst()) { "Query did not return a row" }
        cursor.getDouble(0)
    }

    private companion object {
        const val TEST_DATABASE_NAME = "app-database-migration-test"
        const val VERSION_6 = 6
        const val VERSION_7 = 7
        const val VERSION_8 = 8
    }
}

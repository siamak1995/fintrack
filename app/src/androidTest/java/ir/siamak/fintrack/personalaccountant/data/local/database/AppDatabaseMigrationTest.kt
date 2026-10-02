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

/** Verifies that the non-destructive v6-to-v7 repair preserves legacy installment data. */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName
    )

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

    private fun SupportSQLiteDatabase.columnExists(table: String, column: String): Boolean =
        query("PRAGMA table_info($table)").use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow("name")) == column) return true
            }
            false
        }

    private fun android.database.Cursor.singleLong(): Long = use { cursor ->
        check(cursor.moveToFirst()) { "Query did not return a row" }
        cursor.getLong(0)
    }

    private companion object {
        const val TEST_DATABASE_NAME = "app-database-migration-test"
        const val VERSION_6 = 6
        const val VERSION_7 = 7
    }
}

package ir.siamak.fintrack.common

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/** Framework-only probe can seed an OLD target APK before installing the update.
 * Uses independent fixture files, never the user's fintrack_db / store_fintrack_db.
 */
class UpgradeRetentionTest {
    @Test fun seededAccountingFixtureSurvivesApkReplacement() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val seed = InstrumentationRegistry.getArguments().getString("upgradeFixture") == "seed"
        val personalName = "upgrade-validation-personal.db"
        assumeTrue(seed || context.getDatabasePath(personalName).exists())
        context.openOrCreateDatabase(personalName,Context.MODE_PRIVATE,null).use { db ->
            if(seed) {
                db.execSQL("CREATE TABLE IF NOT EXISTS wallet (id INTEGER PRIMARY KEY, name TEXT, balance INTEGER, contextId INTEGER)")
                db.execSQL("INSERT OR IGNORE INTO wallet VALUES (777,'دادهٔ تست مستقل',123456789,1)")
                db.version = 8
            }
            db.rawQuery("SELECT name,balance,contextId FROM wallet WHERE id=777",null).use {
                assertTrue(it.moveToFirst()); assertEquals("دادهٔ تست مستقل",it.getString(0)); assertEquals(123456789L,it.getLong(1)); assertEquals(1,it.getInt(2))
            }
            assertEquals(8,db.version)
        }
        context.openOrCreateDatabase("upgrade-validation-store.db",Context.MODE_PRIVATE,null).use { db ->
            if(seed) {
                db.execSQL("CREATE TABLE IF NOT EXISTS invoices (id INTEGER PRIMARY KEY, number TEXT, payable INTEGER, received INTEGER)")
                db.execSQL("INSERT OR IGNORE INTO invoices VALUES (777,'TEST-777',999999,111111)")
                db.version = 2
            }
            db.rawQuery("SELECT number,payable,received FROM invoices WHERE id=777",null).use {
                assertTrue(it.moveToFirst()); assertEquals("TEST-777",it.getString(0)); assertEquals(999999L,it.getLong(1)); assertEquals(111111L,it.getLong(2))
            }
            assertEquals(2,db.version)
        }
        val preference = File(context.filesDir,"upgrade-validation-settings.txt")
        if(seed && !preference.exists()) preference.writeText("persian|light|test-context=1")
        assertEquals("persian|light|test-context=1",preference.readText())
    }
}

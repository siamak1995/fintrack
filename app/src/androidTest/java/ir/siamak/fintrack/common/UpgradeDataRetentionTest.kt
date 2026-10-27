package ir.siamak.fintrack.common

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Framework-only seed runs against the PREVIOUS installed APK, verification against the update.
 * Saves hashes in a dedicated noBackup file. Never changes or clears accounting records. */
class UpgradeDataRetentionTest {
    @Test fun existingAccountingRowsSurviveReplacement() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val seed = InstrumentationRegistry.getArguments().getString("preserveExisting") == "seed"
        val file = File(context.noBackupFilesDir,"upgrade-existing-row-hashes.json")
        if(seed) {
            val snapshots = JSONObject()
            listOf("fintrack_db","store_fintrack_db").forEach { name ->
                val path = context.getDatabasePath(name)
                if(path.exists()) SQLiteDatabase.openDatabase(path.path,null,SQLiteDatabase.OPEN_READONLY).use { db ->
                    val tables = JSONObject()
                    db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT IN ('android_metadata','room_master_table')",null).use { cursor ->
                        while(cursor.moveToNext()) {
                            val table = cursor.getString(0)
                            db.rawQuery("SELECT * FROM " + quote(table),null).use { rows ->
                                val columns = rows.columnNames.toList()
                                tables.put(table,JSONObject().put("columns",JSONArray(columns)).put("hashes",JSONArray(hashes(rows))))
                            }
                        }
                    }
                    snapshots.put(name,tables)
                }
            }
            file.writeText(snapshots.toString())
            assertTrue(file.isFile)
        } else {
            if(InstrumentationRegistry.getArguments().getString("preserveExisting") == "verify")
                assertTrue("Seed the previous APK before replacing it",file.isFile)
            org.junit.Assume.assumeTrue("Upgrade-only test requires a pre-update snapshot",file.isFile)
            val snapshots = JSONObject(file.readText())
            snapshots.keys().forEach { name ->
                val path = context.getDatabasePath(name)
                assertTrue("Database missing after update: $name",path.exists())
                SQLiteDatabase.openDatabase(path.path,null,SQLiteDatabase.OPEN_READONLY).use { db ->
                    val tables = snapshots.getJSONObject(name)
                    tables.keys().forEach { table ->
                        val saved = tables.getJSONObject(table)
                        val columns = saved.getJSONArray("columns")
                        val select = (0 until columns.length()).joinToString(",") { quote(columns.getString(it)) }
                        val expected = saved.getJSONArray("hashes")
                        db.rawQuery("SELECT $select FROM " + quote(table),null).use { rows ->
                            val current = hashes(rows).toMutableList()
                            (0 until expected.length()).forEach { index ->
                                assertTrue("Existing financial row changed or lost in $name/$table",current.remove(expected.getString(index)))
                            }
                        }
                    }
                }
            }
        }
    }
    private fun quote(value: String) = "\"" + value.replace("\"","\"\"") + "\""
    private fun hashes(rows: Cursor): List<String> = buildList {
        while(rows.moveToNext()) {
            val digest = MessageDigest.getInstance("SHA-256")
            (0 until rows.columnCount).forEach { column ->
                val value = when(rows.getType(column)) {
                    Cursor.FIELD_TYPE_NULL -> "null"
                    Cursor.FIELD_TYPE_BLOB -> android.util.Base64.encodeToString(rows.getBlob(column),android.util.Base64.NO_WRAP)
                    else -> rows.getString(column)
                }
                digest.update((rows.getType(column).toString() + ":" + value.length + ":" + value + ";").toByteArray(Charsets.UTF_8))
            }
            add(digest.digest().joinToString("") { "%02x".format(it) })
        }
    }
}

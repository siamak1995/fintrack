package ir.siamak.fintrack.common

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import ir.siamak.fintrack.common.core.backup.BackupArchive
import ir.siamak.fintrack.di.DatabaseModule
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.store.data.local.StoreDatabase
import org.junit.Assert.*
import org.junit.Test
import java.io.*
import java.util.zip.*

class BackupArchiveTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    @Test fun fullRestorePreservesBothDatabasesAndSettingsInAnIsolatedSandbox(): Unit = kotlinx.coroutines.runBlocking {
        val folder = File(context.cacheDir,"full-restore-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        val sandbox = object : android.content.ContextWrapper(context) {
            override fun getApplicationContext(): android.content.Context = this
            override fun getFilesDir() = File(folder,"files").apply { mkdirs() }
            override fun getNoBackupFilesDir() = File(folder,"no-backup").apply { mkdirs() }
            override fun getDatabasePath(name: String) = File(folder,"databases/$name").apply { parentFile?.mkdirs() }
        }
        val personal = Room.databaseBuilder(sandbox,AppDatabase::class.java,"fintrack_db").allowMainThreadQueries().build()
        val store = Room.databaseBuilder(sandbox,StoreDatabase::class.java,"store_fintrack_db").allowMainThreadQueries().build()
        try {
            personal.openHelper.writableDatabase.execSQL("INSERT INTO accountant_contexts (id,ownerUserId,type,name,description,isActive,createdAt,updatedAt) VALUES (1,0,'PERSONAL','شخصی محفوظ',NULL,1,0,0)")
            store.openHelper.writableDatabase.execSQL("INSERT INTO stores (id,name,brand,logoPath,phone,address,description) VALUES (1,'فروشگاه محفوظ','برند',NULL,NULL,NULL,NULL)")
            val preferences = File(sandbox.filesDir,"datastore/app_settings.preferences_pb").apply { parentFile?.mkdirs() }
            val preferencesJob = kotlinx.coroutines.SupervisorJob()
            val preferencesScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + preferencesJob)
            val dataStore = androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(scope = preferencesScope,produceFile = { preferences })
            dataStore.updateData { androidx.datastore.preferences.core.preferencesOf(androidx.datastore.preferences.core.booleanPreferencesKey("test-setting") to true) }
            preferencesJob.cancel(); preferencesJob.join()
            val savedPreferences = preferences.readBytes()
            val manager = BackupArchive(sandbox,personal,store)
            val output = ByteArrayOutputStream(); manager.export(output)
            personal.openHelper.writableDatabase.execSQL("UPDATE accountant_contexts SET name='پس از پشتیبان'")
            store.openHelper.writableDatabase.execSQL("UPDATE stores SET name='پس از پشتیبان'")
            manager.stage(ByteArrayInputStream(output.toByteArray()))
            // Staging itself must never replace active data.
            store.openHelper.writableDatabase.query("SELECT name FROM stores").use { it.moveToFirst(); assertEquals("پس از پشتیبان",it.getString(0)) }
            personal.close(); store.close(); preferences.writeText("changed")
            BackupArchive.applyPendingAtStartup(sandbox)
            SQLiteDatabase.openDatabase(sandbox.getDatabasePath("fintrack_db").path,null,SQLiteDatabase.OPEN_READONLY).use { sql ->
                sql.rawQuery("SELECT name FROM accountant_contexts",null).use { assertTrue(it.moveToFirst()); assertEquals("شخصی محفوظ",it.getString(0)) }
            }
            SQLiteDatabase.openDatabase(sandbox.getDatabasePath("store_fintrack_db").path,null,SQLiteDatabase.OPEN_READONLY).use { sql ->
                sql.rawQuery("SELECT name FROM stores",null).use { assertTrue(it.moveToFirst()); assertEquals("فروشگاه محفوظ",it.getString(0)) }
            }
            assertArrayEquals(savedPreferences,preferences.readBytes())
            assertTrue(sandbox.noBackupFilesDir.listFiles().orEmpty().any { it.name.startsWith("before-restore-") })
            // Applying startup a second time does not duplicate data or erase later edits.
            BackupArchive.applyPendingAtStartup(sandbox)
            assertFalse(File(sandbox.noBackupFilesDir,"restore-pending").exists())
        } finally { personal.close(); store.close(); folder.deleteRecursively() }
    }

    @Test fun interruptedMultiFileRestoreRollsBackOriginalsBeforeDatabaseOpen() {
        val folder = File(context.cacheDir,"rollback-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        val sandbox = object : android.content.ContextWrapper(context) {
            override fun getFilesDir() = File(folder,"files").apply { mkdirs() }
            override fun getNoBackupFilesDir() = File(folder,"no-backup").apply { mkdirs() }
            override fun getDatabasePath(name: String) = File(folder,"databases/$name").apply { parentFile?.mkdirs() }
        }
        try {
            val recovery = File(sandbox.noBackupFilesDir,"restore-recovery").apply { mkdirs() }
            val originals = listOf("databases/fintrack_db","databases/fintrack_db-wal","databases/store_fintrack_db")
            originals.forEach { path -> File(recovery,path).apply { parentFile?.mkdirs(); writeText("original:$path") } }
            File(recovery,"originals.txt").writeText(originals.joinToString("\n")); File(recovery,"applying").writeText("1")
            sandbox.getDatabasePath("fintrack_db").writeText("partially replaced")
            BackupArchive.applyPendingAtStartup(sandbox)
            originals.forEach { path -> assertEquals("original:$path",sandbox.getDatabasePath(path.substringAfter('/')).readText()) }
            assertFalse(recovery.exists())
        } finally { folder.deleteRecursively() }
    }
    @Test fun sqliteSnapshotPreservesUncheckpointedDataAndSequenceWithoutTouchingSource() {
        val db = Room.inMemoryDatabaseBuilder(context,StoreDatabase::class.java).allowMainThreadQueries().build()
        val folder = File(context.cacheDir,"backup-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        try {
            val sql = db.openHelper.writableDatabase
            sql.execSQL("INSERT INTO stores (id,name,brand,logoPath,phone,address,description) VALUES (1,'حفظ داده','برند',NULL,NULL,NULL,NULL)")
            sql.execSQL("CREATE TABLE legacy_probe (id INTEGER PRIMARY KEY AUTOINCREMENT, payload BLOB)")
            sql.execSQL("INSERT INTO legacy_probe(id,payload) VALUES (77,?)", arrayOf(byteArrayOf(0,1,-1)))
            sql.execSQL("INSERT INTO legacy_probe(id,payload) VALUES (99,NULL)")
            sql.execSQL("DELETE FROM legacy_probe WHERE id=99")
            val file = File(folder,"snapshot.db")
            BackupArchive.snapshot(db,file)
            SQLiteDatabase.openDatabase(file.path,null,SQLiteDatabase.OPEN_READONLY).use { copy ->
                copy.rawQuery("SELECT name FROM stores WHERE id=1",null).use { assertTrue(it.moveToFirst()); assertEquals("حفظ داده",it.getString(0)) }
                assertEquals(3,copy.version)
                copy.rawQuery("SELECT payload FROM legacy_probe WHERE id=77",null).use { assertTrue(it.moveToFirst()); assertArrayEquals(byteArrayOf(0,1,-1),it.getBlob(0)) }
                copy.rawQuery("SELECT seq FROM sqlite_sequence WHERE name='legacy_probe'",null).use { assertTrue(it.moveToFirst()); assertEquals(99,it.getInt(0)) }
            }
            sql.query("SELECT COUNT(*) FROM stores").use { it.moveToFirst(); assertEquals(1,it.getInt(0)) }
        } finally { db.close(); folder.deleteRecursively() }
    }
    @Test fun archiveRoundTripChecksBothDatabasesAndPreferences() {
        val work = File(context.cacheDir,"backup-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        try {
            val entries = listOf("databases/fintrack_db","databases/store_fintrack_db","files/datastore/app_settings.preferences_pb")
                .associateWith { path -> File(work,path.substringAfterLast('/')).apply { writeBytes(byteArrayOf(0,1,2,127,-1)) } }
            val data = ByteArrayOutputStream().also { BackupArchive.writeArchive(entries,it) }.toByteArray()
            val destination = File(work,"restored").apply { mkdirs() }
            BackupArchive.readArchive(ByteArrayInputStream(data),destination)
            entries.forEach { (path,file) -> assertArrayEquals(file.readBytes(),File(destination,path).readBytes()) }
        } finally { work.deleteRecursively() }
    }
    @Test fun rejectsZipPathTraversalBeforeWritingOutsideStaging() {
        val work = File(context.cacheDir,"backup-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        try {
            val data = ByteArrayOutputStream().also { output -> ZipOutputStream(output).use { zip -> zip.putNextEntry(ZipEntry("../escaped")); zip.write(1); zip.closeEntry() } }.toByteArray()
            try { BackupArchive.readArchive(ByteArrayInputStream(data),work); fail("Invalid archive accepted") }
            catch(expected: Exception) { assertTrue(expected is IllegalArgumentException || expected is java.util.zip.ZipException) }
            assertFalse(File(work.parentFile,"escaped").exists())
        } finally { work.deleteRecursively() }
    }
    @Test fun rejectsTamperedArchive() {
        val work = File(context.cacheDir,"backup-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        try {
            val entries = listOf("databases/fintrack_db","databases/store_fintrack_db").associateWith { File(work,it.substringAfterLast('/')).apply { writeText("original") } }
            val valid = ByteArrayOutputStream().also { BackupArchive.writeArchive(entries,it) }.toByteArray()
            val corrupt = ByteArrayOutputStream()
            ZipOutputStream(corrupt).use { output -> ZipInputStream(ByteArrayInputStream(valid)).use { input ->
                while(true) { val entry = input.nextEntry ?: break; output.putNextEntry(ZipEntry(entry.name)); output.write(if(entry.name == "databases/fintrack_db") "changed".toByteArray() else input.readBytes()); output.closeEntry() }
            } }
            try { BackupArchive.readArchive(ByteArrayInputStream(corrupt.toByteArray()),File(work,"stage")); fail("Tampering accepted") } catch(expected: IllegalArgumentException) { }
        } finally { work.deleteRecursively() }
    }
    @Test fun personalProviderRefusesDowngradeAndPreservesAllData() {
        // A unique, independent test DB. Never opens or deletes fintrack_db / store_fintrack_db.
        val name = "backup-downgrade-test-" + java.util.UUID.randomUUID()
        val providerContext = object : android.content.ContextWrapper(context) {
            override fun getApplicationContext(): android.content.Context = this
            override fun getDatabasePath(requested: String) = if(requested == "fintrack_db") context.getDatabasePath(name) else super.getDatabasePath(requested)
        }
        val db = DatabaseModule.provideAppDatabase(providerContext)
        try {
            db.openHelper.writableDatabase.execSQL("INSERT INTO accountant_contexts (id,ownerUserId,type,name,description,isActive,createdAt,updatedAt) VALUES (1,0,'PERSONAL','محفوظ',NULL,1,0,0)")
            db.openHelper.writableDatabase.version = 11
            db.close()
            val lower = DatabaseModule.provideAppDatabase(providerContext)
            try { lower.openHelper.writableDatabase; fail("Downgrade must not silently delete data") } catch(expected: IllegalStateException) { } finally { lower.close() }
            SQLiteDatabase.openDatabase(context.getDatabasePath(name).path,null,SQLiteDatabase.OPEN_READONLY).use { sql ->
                sql.rawQuery("SELECT name FROM accountant_contexts WHERE id=1",null).use { assertTrue(it.moveToFirst()); assertEquals("محفوظ",it.getString(0)) }
            }
        } finally { db.close(); context.deleteDatabase(name) }
    }
}

package ir.siamak.fintrack.common.core.backup

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ir.siamak.fintrack.di.PersonalAccountingDatabase
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.store.data.local.StoreDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.io.*
import java.security.MessageDigest
import java.util.zip.*
import javax.inject.Inject
import javax.inject.Singleton

/** Explicit, offline backup. Never runs destructive recovery on an ordinary database-open failure. */
@Singleton
class BackupArchive @Inject constructor(
    @ApplicationContext private val context: Context,
    @PersonalAccountingDatabase private val personal: AppDatabase,
    private val store: StoreDatabase
) {
    suspend fun export(output: OutputStream) = withContext(Dispatchers.IO) {
        val work = File(context.noBackupFilesDir, "backup-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        try {
            val entries = linkedMapOf<String, File>()
            snapshot(personal, File(work,"fintrack_db")); entries["databases/fintrack_db"] = File(work,"fintrack_db")
            snapshot(store, File(work,"store_fintrack_db")); entries["databases/store_fintrack_db"] = File(work,"store_fintrack_db")
            PREFERENCE_PATHS.forEach { path ->
                val file = File(context.filesDir, path)
                if(file.exists()) {
                    // DataStore commits by atomic rename: copy a complete committed file, not its temp file.
                    val copy = File(work, file.name); file.inputStream().use { input -> copy.outputStream().use { input.copyTo(it) } }
                    entries["files/$path"] = copy
                }
            }
            writeArchive(entries, output)
        } finally { work.deleteRecursively() }
    }

    /** Validates an archive off to the side. No active file is replaced before explicit restart. */
    suspend fun stage(input: InputStream) = withContext(Dispatchers.IO) {
        val stage = File(context.noBackupFilesDir,"restore-validation-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        try {
            readArchive(input, stage)
            validateDatabase(File(stage,"databases/fintrack_db"), 8, "wallet")
            validateDatabase(File(stage,"databases/store_fintrack_db"), 2, "invoices")
            PREFERENCE_PATHS.forEach { path ->
                val preferences = File(stage,"files/$path")
                if(preferences.exists()) validatePreferences(preferences)
            }
            val pending = File(context.noBackupFilesDir,"restore-pending")
            check(!pending.exists()) { "یک بازیابی در انتظار راه‌اندازی مجدد است" }
            check(stage.renameTo(pending)) { "آماده‌سازی بازیابی ناموفق بود" }
        } finally { if(stage.exists()) stage.deleteRecursively() }
    }

    companion object {
        private val PREFERENCE_PATHS = listOf("datastore/app_settings.preferences_pb", "datastore/fintrack_preferences.preferences_pb")
        private val DATABASE_PATHS = listOf("databases/fintrack_db", "databases/store_fintrack_db")
        private val ALLOWED = DATABASE_PATHS + PREFERENCE_PATHS.map { "files/$it" }
        private const val LIMIT = 256L * 1024 * 1024
        private fun quoted(value: String) = "\"" + value.replace("\"", "\"\"") + "\""
        private fun hash(file: File): String {
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { stream -> val buffer = ByteArray(16384); while(true) { val size = stream.read(buffer); if(size < 0) break; digest.update(buffer,0,size) } }
            return digest.digest().joinToString("") { "%02x".format(it) }
        }

        internal fun snapshot(room: RoomDatabase, target: File) {
            room.runInTransaction {
                val source = room.openHelper.writableDatabase
                SQLiteDatabase.openOrCreateDatabase(target, null).use { destination ->
                    destination.beginTransaction()
                    try {
                        val objects = mutableListOf<Triple<String,String,String>>()
                        source.query("SELECT type,name,sql FROM sqlite_master WHERE sql IS NOT NULL AND name NOT LIKE 'sqlite_%' ORDER BY CASE type WHEN 'table' THEN 0 WHEN 'index' THEN 1 ELSE 2 END").use { cursor ->
                            while(cursor.moveToNext()) objects += Triple(cursor.getString(0),cursor.getString(1),cursor.getString(2))
                        }
                        objects.filter { it.first == "table" }.forEach { (_,name,sql) ->
                            if(name == "android_metadata") destination.execSQL("DROP TABLE IF EXISTS android_metadata")
                            destination.execSQL(sql)
                            source.query("SELECT * FROM " + quoted(name)).use { cursor ->
                                val insert = "INSERT INTO " + quoted(name) + " (" + cursor.columnNames.joinToString(",") { quoted(it) } + ") VALUES (" + List(cursor.columnCount) { "?" }.joinToString(",") + ")"
                                destination.compileStatement(insert).use { statement ->
                                    while(cursor.moveToNext()) {
                                        statement.clearBindings()
                                        for(index in 0 until cursor.columnCount) when(cursor.getType(index)) {
                                            Cursor.FIELD_TYPE_NULL -> statement.bindNull(index+1)
                                            Cursor.FIELD_TYPE_INTEGER -> statement.bindLong(index+1,cursor.getLong(index))
                                            Cursor.FIELD_TYPE_FLOAT -> statement.bindDouble(index+1,cursor.getDouble(index))
                                            Cursor.FIELD_TYPE_BLOB -> statement.bindBlob(index+1,cursor.getBlob(index))
                                            else -> statement.bindString(index+1,cursor.getString(index))
                                        }
                                        statement.executeInsert()
                                    }
                                }
                            }
                        }
                        // Preserve AUTOINCREMENT high-water marks, including ids of deleted rows.
                        source.query("SELECT name FROM sqlite_master WHERE name='sqlite_sequence'").use { cursor ->
                            if(cursor.moveToFirst()) source.query("SELECT name,seq FROM sqlite_sequence").use { sequence ->
                                while(sequence.moveToNext()) {
                                    destination.execSQL("DELETE FROM sqlite_sequence WHERE name=?", arrayOf(sequence.getString(0)))
                                    destination.execSQL("INSERT INTO sqlite_sequence(name,seq) VALUES(?,?)", arrayOf(sequence.getString(0),sequence.getLong(1)))
                                }
                            }
                        }
                        objects.filter { it.first != "table" }.forEach { destination.execSQL(it.third) }
                        destination.version = source.version
                        destination.setTransactionSuccessful()
                    } finally { destination.endTransaction() }
                }
            }
        }

        internal fun writeArchive(entries: Map<String,File>, output: OutputStream) {
            val manifest = JSONObject().put("format",1).put("applicationId","ir.siamak.fintrack")
            val checksums = JSONObject(); entries.forEach { (name,file) -> checksums.put(name, hash(file)) }; manifest.put("sha256", checksums)
            ZipOutputStream(output).use { zip ->
                zip.putNextEntry(ZipEntry("manifest.json")); zip.write(manifest.toString().toByteArray(Charsets.UTF_8)); zip.closeEntry()
                entries.forEach { (name,file) -> zip.putNextEntry(ZipEntry(name)); file.inputStream().use { it.copyTo(zip) }; zip.closeEntry() }
            }
        }

        internal fun readArchive(input: InputStream, stage: File) {
            val seen = mutableSetOf<String>(); var bytes = 0L
            ZipInputStream(input).use { zip ->
                while(true) {
                    val entry = zip.nextEntry ?: break
                    require(!entry.isDirectory && (entry.name == "manifest.json" || entry.name in ALLOWED) && seen.add(entry.name)) { "ساختار فایل پشتیبان معتبر نیست" }
                    val target = File(stage,entry.name); target.parentFile?.mkdirs()
                    target.outputStream().use { output ->
                        val buffer = ByteArray(16384)
                        var entryBytes = 0L
                        val entryLimit = if(entry.name == "manifest.json") 65536L else if(entry.name.startsWith("files/")) 2L*1024*1024 else LIMIT
                        while(true) { val count = zip.read(buffer); if(count < 0) break; bytes += count; entryBytes += count; require(bytes <= LIMIT && entryBytes <= entryLimit) { "حجم پشتیبان بیش از حد مجاز است" }; output.write(buffer,0,count) }
                    }
                }
            }
            val manifest = JSONObject(File(stage,"manifest.json").readText())
            require(manifest.getInt("format") == 1 && manifest.getString("applicationId") == "ir.siamak.fintrack")
            val expected = manifest.getJSONObject("sha256")
            require(expected.keys().asSequence().toSet() == seen - "manifest.json")
            require(DATABASE_PATHS.all { it in seen }) { "هر دو پایگاه داده باید در پشتیبان موجود باشند" }
            (seen - "manifest.json").forEach { name -> require(expected.getString(name) == hash(File(stage,name))) { "پشتیبان آسیب دیده است" } }
        }

        private fun verifyStaged(stage: File) {
            val manifest = JSONObject(File(stage,"manifest.json").readText())
            require(manifest.getInt("format") == 1 && manifest.getString("applicationId") == "ir.siamak.fintrack")
            val expected = manifest.getJSONObject("sha256")
            val names = expected.keys().asSequence().toSet()
            require(names.all { it in ALLOWED } && DATABASE_PATHS.all { it in names })
            names.forEach { path -> require(File(stage,path).isFile && hash(File(stage,path)) == expected.getString(path)) { "فایل آمادهٔ بازیابی تغییر کرده یا آسیب دیده است" } }
        }

        private fun validateDatabase(file: File, version: Int, table: String) {
            SQLiteDatabase.openDatabase(file.path,null,SQLiteDatabase.OPEN_READONLY).use { db ->
                require(db.version == version) { "نسخهٔ پشتیبان با این برنامه سازگار نیست؛ از نسخهٔ سازگار برای بازیابی استفاده کنید" }
                db.rawQuery("PRAGMA integrity_check",null).use { require(it.moveToFirst() && it.getString(0) == "ok") { "صحت پایگاه داده تأیید نشد" } }
                db.rawQuery("SELECT COUNT(*) FROM " + quoted(table),null).use { require(it.moveToFirst()) }
                val expected = if(version == 8) "f1622432820d3af5b3b49ac7aac91386" else "9652f179120c420dd68f28fecf641d87"
                db.rawQuery("SELECT identity_hash FROM room_master_table WHERE id=42",null).use {
                    require(it.moveToFirst() && it.getString(0) == expected) { "Schema پشتیبان با نسخهٔ نصب‌شده سازگار نیست" }
                }
            }
        }

        private suspend fun validatePreferences(file: File) {
            val job = SupervisorJob()
            val scope = CoroutineScope(Dispatchers.IO + job)
            try {
                androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(scope = scope, produceFile = { file }).data.first()
            } finally { scope.cancel(); job.join() }
        }

        /** The application shell must await completion before resolving Room/DataStore dependencies. */
        fun applyPendingAtStartup(context: Context) {
            val pending = File(context.noBackupFilesDir,"restore-pending")
            val recovery = File(context.noBackupFilesDir,"restore-recovery")
            fun target(path: String) = if(path.startsWith("databases/")) context.getDatabasePath(path.substringAfter("databases/")) else File(context.filesDir,path.substringAfter("files/"))
            val paths = ALLOWED + DATABASE_PATHS.flatMap { listOf("$it-wal", "$it-shm", "$it-journal") }
            fun rollback() {
                val manifest = File(recovery,"originals.txt").readLines().toSet()
                paths.forEach { path ->
                    val file = target(path)
                    if(path in manifest) { file.parentFile?.mkdirs(); File(recovery,path).copyTo(file,overwrite=true) }
                    else if(file.exists()) check(file.delete())
                }
            }
            if(File(recovery,"applying").exists()) {
                rollback(); recovery.renameTo(File(context.noBackupFilesDir,"restore-rolled-back-" + System.currentTimeMillis()))
                if(pending.exists()) pending.renameTo(File(context.noBackupFilesDir,"restore-failed-" + System.currentTimeMillis()))
                return
            }
            if(!pending.exists()) return
            // Validate again before touching any active file; interrupted/corrupt staging is never installed.
            verifyStaged(pending)
            validateDatabase(File(pending,DATABASE_PATHS[0]),8,"wallet")
            validateDatabase(File(pending,DATABASE_PATHS[1]),2,"invoices")
            recovery.mkdirs()
            val originals = paths.filter { target(it).exists() }
            originals.forEach { path -> File(recovery,path).parentFile?.mkdirs(); target(path).copyTo(File(recovery,path),overwrite=true) }
            File(recovery,"originals.txt").writeText(originals.joinToString("\n"))
            File(recovery,"applying").writeText("1")
            try {
                paths.forEach { path ->
                    val from = File(pending,path); val to = target(path)
                    if(from.exists()) { to.parentFile?.mkdirs(); from.copyTo(to,overwrite=true) }
                    else if(to.exists()) check(to.delete())
                }
                check(pending.renameTo(File(context.noBackupFilesDir,"restore-applied-" + System.currentTimeMillis())))
                check(File(recovery,"applying").delete())
                check(recovery.renameTo(File(context.noBackupFilesDir,"before-restore-" + System.currentTimeMillis())))
            } catch(error: Exception) { rollback(); throw error }
        }
    }
}

package ir.siamak.fintrack.common.core.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class BackupUiState(val busy: Boolean = false, val message: String? = null, val restartRequired: Boolean = false)

@HiltViewModel
class BackupViewModel @Inject constructor(@ApplicationContext private val context: Context, private val archive: BackupArchive) : ViewModel() {
    private val mutable = MutableStateFlow(BackupUiState())
    val state = mutable.asStateFlow()
    fun export(uri: Uri) = perform {
        withContext(Dispatchers.IO) { requireNotNull(context.contentResolver.openOutputStream(uri)).use { archive.export(it) } }
        mutable.value = BackupUiState(message = "نسخهٔ پشتیبان هر دو حسابدار و تنظیمات ذخیره شد. فایل را در محل امن نگه دارید.")
    }
    fun restore(uri: Uri) = perform {
        // Always retain a recoverable archive of the current data before preparing a replacement.
        val safety = java.io.File(context.noBackupFilesDir,"before-restore-" + System.currentTimeMillis() + ".fintrack")
        withContext(Dispatchers.IO) {
            safety.outputStream().use { archive.export(it) }
            requireNotNull(context.contentResolver.openInputStream(uri)).use { archive.stage(it) }
        }
        mutable.value = BackupUiState(message = "صحت فایل بررسی شد. برای اعمال بازیابی، برنامه را ببندید و دوباره باز کنید. دادهٔ فعلی نیز پشتیبان‌گیری شده است.", restartRequired = true)
    }
    private fun perform(block: suspend () -> Unit) {
        if(mutable.value.busy || mutable.value.restartRequired) return
        mutable.value = BackupUiState(busy = true)
        viewModelScope.launch {
            try { block() }
            catch(cancelled: CancellationException) { mutable.value = BackupUiState(); throw cancelled }
            catch(error: Exception) { mutable.value = BackupUiState(message = "عملیات انجام نشد؛ دادهٔ جاری تغییر نکرد: " + (error.message ?: "خطای خواندن یا نوشتن فایل")) }
        }
    }
}

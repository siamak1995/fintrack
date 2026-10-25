package ir.siamak.fintrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * کلاس Application اصلی پروژه.
 *
 * این کلاس نقطه شروع Hilt در اپلیکیشن است و باید
 * در فایل Manifest نیز به عنوان application name ثبت شود.
 */
@HiltAndroidApp
class FinTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // The shell waits before resolving data-dependent ViewModels. Large restores must not ANR.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                ir.siamak.fintrack.common.core.backup.BackupArchive.applyPendingAtStartup(this@FinTrackApplication)
                mutableRestoreState.value = RestoreStartupState(checking = false)
            } catch(error: Exception) {
                android.util.Log.e("FinTrackBackup", "Restore failed; original data retained for recovery", error)
                mutableRestoreState.value = RestoreStartupState(false,
                    "بازیابی کامل نشد. برای حفاظت از داده‌ها ورود متوقف شده است؛ نسخهٔ قبلی در پشتیبان داخلی نگهداری می‌شود. " + error.message)
            }
        }
    }
    companion object {
        private val mutableRestoreState = MutableStateFlow(RestoreStartupState())
        val restoreState = mutableRestoreState.asStateFlow()
    }
}

data class RestoreStartupState(val checking: Boolean = true, val error: String? = null)


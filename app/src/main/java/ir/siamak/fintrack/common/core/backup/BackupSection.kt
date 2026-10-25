package ir.siamak.fintrack.common.core.backup

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BackupSection(vm: BackupViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var candidate by remember { mutableStateOf<Uri?>(null) }
    var exportConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    fun closeForRestore() {
        (context as? Activity)?.finishAffinity()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
    val create = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { it?.let(vm::export) }
    val open = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { candidate = it }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("پشتیبان‌گیری و بازیابی", style = MaterialTheme.typography.titleLarge)
            Text("به‌روزرسانی را بدون حذف برنامه نصب کنید. پیش از حذف برنامه یا تعویض دستگاه، پشتیبان را بیرون از فضای برنامه ذخیره کنید.")
            Text("شامل هر دو پایگاه داده و تنظیمات، از جمله تنظیمات قفل است. تصاویر انتخاب‌شده از برنامه‌های دیگر ممکن است پس از نصب مجدد نیاز به انتخاب دوباره داشته باشند.", style = MaterialTheme.typography.bodySmall)
            Button(enabled = !state.busy && !state.restartRequired, onClick = { exportConfirmation = true }) { Text("ذخیرهٔ نسخهٔ پشتیبان") }
            OutlinedButton(enabled = !state.busy && !state.restartRequired, onClick = { open.launch(arrayOf("application/zip", "application/octet-stream")) }) { Text("انتخاب فایل برای بازیابی") }
            if(state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.message?.let { Text(it) }
            if(state.restartRequired) Button(onClick = ::closeForRestore) { Text("بستن برنامه برای اعمال بازیابی؛ سپس دوباره باز کنید") }
        }
    }
    if(state.restartRequired) AlertDialog(onDismissRequest = {},
        properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = { Text("راه‌اندازی مجدد برای بازیابی") },
        text = { Text("برای جلوگیری از ثبت دادهٔ جدید بین آماده‌سازی و بازیابی، ابتدا برنامه را ببندید و از آیکون آن دوباره باز کنید. دادهٔ پیش از بازیابی پشتیبان‌گیری شده است.") },
        confirmButton = { TextButton(onClick = ::closeForRestore) { Text("بستن برنامه") } })
    if(exportConfirmation) AlertDialog(onDismissRequest = { exportConfirmation = false },
        title = { Text("محافظت از اطلاعات مالی") },
        text = { Text("فایل پشتیبان رمزگذاری نشده و اطلاعات مالی و تنظیمات قفل را در بر دارد. آن را فقط در محل امن شخصی ذخیره کنید و به دیگران ندهید.") },
        confirmButton = { TextButton(onClick = { exportConfirmation = false; create.launch("FinTrack-" + java.time.LocalDate.now() + ".zip") }) { Text("انتخاب محل امن") } },
        dismissButton = { TextButton(onClick = { exportConfirmation = false }) { Text("انصراف") } })
    candidate?.let { uri -> AlertDialog(onDismissRequest = { candidate = null }, title = { Text("تأیید بازیابی هر دو حسابدار") },
        text = { Text("داده‌ها و تنظیمات فعلی با محتوای پشتیبان جایگزین می‌شوند؛ این کار ادغام نیست. ابتدا یک پشتیبان داخلی از وضع فعلی نگه داشته می‌شود. پس از اعتبارسنجی باید برنامه را ببندید و دوباره باز کنید.") },
        confirmButton = { TextButton(onClick = { candidate = null; vm.restore(uri) }) { Text("بررسی و آماده‌سازی بازیابی") } },
        dismissButton = { TextButton(onClick = { candidate = null }) { Text("انصراف") } }) }
}

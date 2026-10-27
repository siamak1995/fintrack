package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.domain.analytics.loanSchedule
import ir.siamak.fintrack.store.presentation.components.*
import java.time.*

@Composable fun InstallmentScreen(state: InstallmentUiState, onAddInstallmentClick: () -> Unit,
    onEditInstallmentClick: (Long) -> Unit, reportOnly: Boolean = false, onBack: () -> Unit = {}, onSettings: () -> Unit = {}) {
    var selected by rememberSaveable { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    val settings = ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.LocalAppSettings.current
    val enabledInSettings = settings.notificationEnabled && settings.installmentReminder
    var allowed by remember { mutableStateOf(Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed = it }
    StorePage(if(reportOnly) "گزارش وام و اقساط" else "وام و اقساط", state.isLoading, state.error) {
        TextButton(onClick = onBack) { Text("بازگشت") }
        if(!reportOnly) Button(onClick = onAddInstallmentClick, modifier = Modifier.fillMaxWidth()) { Text("ثبت وام / قسط جدید") }
        FormSection("یادآوری سررسید") {
            Text(if(!enabledInSettings) "یادآوری سررسید در تنظیمات برنامه خاموش است." else if(allowed) "مجوز اعلان فعال است؛ سررسیدها از سه روز قبل و پس از سررسید یادآوری می‌شوند." else "برای دریافت یادآوری سررسید اقساط، اعلان‌ها را فعال کنید.", style = MaterialTheme.typography.bodySmall)
            if(!enabledInSettings) FilledTonalButton(onClick = onSettings,modifier = Modifier.fillMaxWidth()) { Text("تنظیمات یادآوری") }
            if(!allowed && Build.VERSION.SDK_INT >= 33) FilledTonalButton(onClick = { permission.launch(Manifest.permission.POST_NOTIFICATIONS) }, modifier = Modifier.fillMaxWidth()) { Text("فعال‌سازی اعلان سررسید") }
        }
        if(state.installments.isEmpty()) Text("هنوز وام یا قسطی ثبت نشده است.")
        if(reportOnly && state.installments.isNotEmpty()) {
            ChoiceEntry("وام", state.installments.firstOrNull { it.id == selected }?.title ?: "همه وام‌ها",
                listOf(null to "همه وام‌ها") + state.installments.map { it.id to it.title }) { selected = it }
            val rows = state.installments.filter { selected == null || it.id == selected }
            DataRow("جمع مانده", quantity(rows.sumOf { loanSchedule(it).remainingAmount }) + " تومان", true)
            DataRow("اقساط باقی‌مانده",quantity(rows.sumOf { loanSchedule(it).remainingCount }))
        }
        state.installments.filter { !reportOnly || selected == null || it.id == selected }.forEach { loan ->
            LoanCard(loan) { onEditInstallmentClick(loan.id) }
        }
    }
}

@Composable private fun LoanCard(loan: Installment, edit: () -> Unit) {
    val schedule = loanSchedule(loan)
    fun date(value: Long) = dateLabel(Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate().toString())
    FormSection(loan.title) {
        DataRow("کل بازپرداخت",quantity(loan.totalAmount) + " تومان")
        DataRow("پرداخت‌شده",quantity(loan.paidAmount) + " تومان")
        DataRow("مانده",quantity(schedule.remainingAmount) + " تومان",true)
        DataRow("اقساط باقی‌مانده",quantity(schedule.remainingCount) + " از " + quantity(loan.installmentCount))
        DataRow("سررسید بعدی",schedule.nextDue?.toString()?.let(::dateLabel) ?: "تسویه شده")
        if(schedule.nextDue != null) DataRow("مبلغ پرداخت بعدی",quantity(schedule.nextAmount) + " تومان")
        DataRow("شروع وام",date(loan.startDate))
        DataRow("اولین سررسید",date(loan.dueDate))
        DataRow("پایان برنامه",dateLabel(schedule.end.toString()))
        DataRow("پرداخت خودکار بانکی",if(loan.automaticPayment) "بله" else "خیر")
        loan.note?.let { Text(it,style = MaterialTheme.typography.bodySmall) }
        FilledTonalButton(onClick = edit, modifier = Modifier.fillMaxWidth()) { Text("ویرایش / به‌روزرسانی پرداخت") }
    }
}

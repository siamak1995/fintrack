package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.add_edit_installment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import ir.siamak.fintrack.store.presentation.components.*
import java.time.*

@Composable fun AddEditInstallmentsScreen(installmentId: Long?, onBack: () -> Unit,
    viewModel: AddEditInstallmentsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(installmentId) { viewModel.load(installmentId) }
    LaunchedEffect(state.isSaved) { if(state.isSaved) onBack() }
    fun date(value: Long) = Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate().toString()
    fun millis(value: String) = LocalDate.parse(value).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    StorePage(if(installmentId == null) "ثبت وام / قسط" else "ویرایش وام / قسط", error = state.error, bottomBar = {
        Button(onClick = { viewModel.onEvent(AddEditInstallmentEvent.Save) }, enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text(if(state.isLoading) "در حال ذخیره…" else "ذخیره") }
        FilledTonalButton(onClick = onBack, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) { Text("انصراف") }
    }) {
        Entry("عنوان وام / قسط *",state.title,{ v -> viewModel.edit { it.copy(title = v) } })
        FormSection("مبالغ و برنامه پرداخت") {
            Entry("جمع کل بازپرداخت · تومان *",state.totalAmountRaw,{ v -> viewModel.edit { it.copy(totalAmountRaw = v) } },number = true)
            Entry("مبلغ پرداخت‌شده تا امروز · تومان",state.paidAmountRaw,{ v -> viewModel.edit { it.copy(paidAmountRaw = v) } },number = true)
            Entry("تعداد کل اقساط ماهانه *",state.installmentCountRaw,{ v -> viewModel.edit { it.copy(installmentCountRaw = v) } },number = true)
            DateEntry("شروع وام",date(state.startDate)) { v -> viewModel.edit { it.copy(startDate = millis(v)) } }
            DateEntry("اولین سررسید",date(state.dueDate)) { v -> viewModel.edit { it.copy(dueDate = millis(v)) } }
            ChoiceEntry("حساب پرداخت",state.wallets.firstOrNull { it.id == state.walletId }?.name ?: "انتخاب حساب",state.wallets.map { it.id to it.name }) { id -> viewModel.edit { it.copy(walletId = id) } }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.automaticPayment,{ v -> viewModel.edit { it.copy(automaticPayment = v) } })
                Text("پرداخت خودکار در بانک فعال است")
            }
            Text("این گزینه وضعیت برداشت خودکار بانک را ثبت می‌کند. پرداخت‌شده را پس از برداشت واقعی به‌روز کنید.",style = MaterialTheme.typography.bodySmall)
        }
        Entry("یادداشت",state.note,{ v -> viewModel.edit { it.copy(note = v) } },lines = 2)
    }
}

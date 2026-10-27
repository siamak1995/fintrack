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
import androidx.compose.runtime.saveable.rememberSaveable
import ir.siamak.fintrack.personalaccountant.domain.analytics.*
import java.math.BigDecimal
import java.math.RoundingMode

@Composable fun AddEditInstallmentsScreen(installmentId: Long?, onBack: () -> Unit,
    viewModel: AddEditInstallmentsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var calculatorOpen by rememberSaveable { mutableStateOf(false) }
    val count = state.installmentCountRaw.toIntOrNull()
    val calculation = remember(state.principalRaw,state.annualRateRaw,count,state.interestMethod) { runCatching {
        LoanCalculator.calculate(state.principalRaw.toBigDecimal(),state.annualRateRaw.toBigDecimal(),count ?: 0,state.interestMethod)
    } }
    val total = if(state.manualContract) state.totalAmountRaw.toDoubleOrNull() else calculation.getOrNull()?.total?.toDouble()
    val monthly = if(state.manualContract) state.installmentAmountRaw.toDoubleOrNull() else calculation.getOrNull()?.payment?.toDouble()
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
        OutlinedButton(onClick = { calculatorOpen = true },enabled = !state.isLoading,modifier = Modifier.fillMaxWidth()) { Text("ماشین‌حساب اقساط وام") }
        FormSection("مبالغ و برنامه پرداخت") {
            Entry("اصل وام · تومان",state.principalRaw,{ v -> viewModel.edit { it.copy(principalRaw = v) } },number = true,amount = true)
            Entry("نرخ سود سالانه · درصد",state.annualRateRaw,{ v -> viewModel.edit { it.copy(annualRateRaw = v) } },number = true,amount = false)
            Entry("تعداد کل اقساط ماهانه *",state.installmentCountRaw,{ v -> viewModel.edit {
                val n = v.toIntOrNull()
                it.copy(installmentCountRaw = v,installmentAmountRaw = if(n != null && n > 0 && it.manualContract)
                    it.totalAmountRaw.toBigDecimalOrNull()?.divide(BigDecimal(n),2,RoundingMode.HALF_UP)?.toPlainString().orEmpty() else it.installmentAmountRaw)
            } },number = true,amount = false)
            ChoiceEntry("روش محاسبه وام",state.interestMethod.label,LoanInterestMethod.entries.map { it to it.label }) { method ->
                viewModel.edit { it.copy(interestMethod = method,
                    totalAmountRaw = if(method == LoanInterestMethod.CONTRACT) calculation.getOrNull()?.total?.toPlainString() ?: it.totalAmountRaw else it.totalAmountRaw,
                    installmentAmountRaw = if(method == LoanInterestMethod.CONTRACT) calculation.getOrNull()?.payment?.toPlainString() ?: it.installmentAmountRaw else it.installmentAmountRaw) }
            }
            if(state.manualContract) {
                Entry("جمع کل بازپرداخت · تومان *",state.totalAmountRaw,{ v -> viewModel.edit { it.copy(totalAmountRaw = v,
                    installmentAmountRaw = if(count != null && count > 0) v.toBigDecimalOrNull()?.divide(BigDecimal(count),2,RoundingMode.HALF_UP)?.toPlainString().orEmpty() else "") } },number = true)
                Entry("مبلغ هر قسط · تومان *",state.installmentAmountRaw,{ v -> viewModel.edit { it.copy(installmentAmountRaw = v,
                    totalAmountRaw = if(count != null && count > 0) v.toBigDecimalOrNull()?.multiply(BigDecimal(count))?.toPlainString().orEmpty() else it.totalAmountRaw) } },number = true)
            } else {
                DataRow("مبلغ هر قسط",monthly?.let { quantity(it) + " تومان" } ?: "اصل وام، درصد و تعداد را وارد کنید")
                DataRow("کل بازپرداخت با سود",total?.let { quantity(it) + " تومان" } ?: "—")
                Text("نرخ واردشده سالانه است. در سود ساده، سود بر اصل وام و مدت محاسبه می‌شود؛ کارمزد و اختلاف بانک را در روش قرارداد وارد کنید.",style = MaterialTheme.typography.bodySmall)
            }
            Entry("پرداخت‌های پیش از ثبت در برنامه · تومان",state.paidAmountRaw,{ v -> viewModel.edit { it.copy(paidAmountRaw = v) } },number = true)
            DataRow("پرداخت ثبت‌شده در تراکنش‌ها",quantity(state.linkedPaidAmount) + " تومان")
            DataRow("مانده کل وام",total?.let { quantity(it - (state.paidAmountRaw.toDoubleOrNull() ?: 0.0) - state.linkedPaidAmount) + " تومان" } ?: "—",true)
            Text("برای پرداخت جدید، تراکنش هزینه ثبت کنید و وام مربوط را انتخاب کنید؛ مبلغ بالا فقط سابقه پرداخت پیش از ثبت در برنامه است و موجودی حساب را تغییر نمی‌دهد.",style = MaterialTheme.typography.bodySmall)
            DateEntry("شروع وام",date(state.startDate)) { v -> viewModel.edit { it.copy(startDate = millis(v)) } }
            DateEntry("اولین سررسید",date(state.dueDate)) { v -> viewModel.edit { it.copy(dueDate = millis(v)) } }
            if(count != null && count in 1..600) DataRow("تاریخ اتمام وام",dateLabel(loanDueDate(Instant.ofEpochMilli(state.dueDate).atZone(ZoneId.systemDefault()).toLocalDate(),count-1).toString()))
            ChoiceEntry("حساب پرداخت",state.wallets.firstOrNull { it.id == state.walletId }?.name ?: "انتخاب حساب",state.wallets.map { it.id to it.name }) { id -> viewModel.edit { it.copy(walletId = id) } }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.automaticPayment,{ v -> viewModel.edit { it.copy(automaticPayment = v) } })
                Text("پرداخت خودکار در بانک فعال است")
            }
            Text("این گزینه وضعیت برداشت خودکار بانک را ثبت می‌کند. پس از برداشت واقعی، پیامک یا تراکنش هزینه را به این وام متصل کنید.",style = MaterialTheme.typography.bodySmall)
        }
        Entry("یادداشت",state.note,{ v -> viewModel.edit { it.copy(note = v) } },lines = 2)
    }
    if(calculatorOpen) LoanCalculatorDialog(state.principalRaw,state.annualRateRaw,state.installmentCountRaw,state.interestMethod,state.installmentAmountRaw) { calculatorOpen = false }
}

@Composable private fun LoanCalculatorDialog(initialPrincipal: String, initialRate: String, initialCount: String, initialMethod: LoanInterestMethod, initialPayment: String, dismiss: () -> Unit) {
    var principal by rememberSaveable { mutableStateOf(initialPrincipal) }
    var rate by rememberSaveable { mutableStateOf(initialRate) }
    var count by rememberSaveable { mutableStateOf(initialCount) }
    var method by rememberSaveable { mutableStateOf(initialMethod) }
    var payment by rememberSaveable { mutableStateOf(initialPayment) }
    val result = remember(principal,rate,count,method,payment) { runCatching {
        if(method == LoanInterestMethod.CONTRACT) {
            val n = count.toInt()
            val p = principal.toBigDecimal()
            val monthly = payment.toBigDecimal()
            val total = monthly.multiply(BigDecimal(n))
            require(n in 1..600 && p.signum() > 0 && monthly.signum() > 0 && total >= p && total <= LoanCalculator.limit)
            LoanCalculation(monthly,total,total.subtract(p))
        } else LoanCalculator.calculate(principal.toBigDecimal(),rate.toBigDecimal(),count.toInt(),method)
    } }
    androidx.compose.ui.window.Dialog(onDismissRequest = dismiss) {
        Surface(shape = MaterialTheme.shapes.large,modifier = Modifier.fillMaxWidth().fillMaxHeight(.9f)) {
            StorePage("محاسبه اقساط",bottomBar = { Button(onClick = dismiss,modifier = Modifier.fillMaxWidth()) { Text("بستن") } }) {
                Text("فقط محاسبه و نمایش؛ وام یا تراکنشی ذخیره نمی‌شود.")
                Entry("اصل وام · تومان",principal,{ principal = it },number = true,amount = true)
                ChoiceEntry("روش محاسبه",method.label,LoanInterestMethod.entries.map { it to it.label }) { method = it }
                if(method == LoanInterestMethod.CONTRACT) Entry("مبلغ هر قسط طبق قرارداد · تومان",payment,{ payment = it },number = true,amount = true)
                else Entry("نرخ سود سالانه · درصد",rate,{ rate = it },number = true,amount = false)
                Entry("تعداد اقساط ماهانه",count,{ count = it },number = true,amount = false)
                result.getOrNull()?.let {
                    DataRow("مبلغ هر قسط",quantity(it.payment) + " تومان",true)
                    DataRow("کل سود",quantity(it.interest) + " تومان")
                    DataRow("کل بازپرداخت",quantity(it.total) + " تومان")
                } ?: Text("اصل وام مثبت، نرخ سالانه بین صفر و صد، و تعداد اقساط بین یک و ۶۰۰ وارد کنید.")
            }
        }
    }
}

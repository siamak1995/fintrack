package ir.siamak.fintrack.personalaccountant.presentation.banksms

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import ir.siamak.fintrack.personalaccountant.banksms.*
import ir.siamak.fintrack.store.presentation.components.*
import java.time.*

@Composable fun BankSmsScreen(onBack: () -> Unit, vm: BankSmsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var disclosure by remember { mutableStateOf(false) }
    var permissionMessage by remember { mutableStateOf<String?>(null) }
    var paste by rememberSaveable { mutableStateOf("") }
    var reviewingId by rememberSaveable { mutableStateOf<String?>(null) }
    var ignoringId by rememberSaveable { mutableStateOf<String?>(null) }
    var showBlocked by rememberSaveable { mutableStateOf(false) }
    val receiverPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        vm.enable(granted)
        permissionMessage = if(granted) null else "دسترسی پیامک فعال نشد. می‌توانید متن پیامک را دستی وارد کنید. در بعضی نصب‌ها این مجوز در دسترس نیست."
    }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    if(showBlocked) {
        StorePage("فهرست سیاه پیامک",state.loading,state.error) {
            TextButton(onClick = { showBlocked = false }) { Text("بازگشت به پیامک‌ها") }
            Text("پیامک‌های این فرستنده‌ها وارد فهرست بررسی نمی‌شوند. با رفع مسدودی، پیامک‌های جدید دوباره بررسی می‌شوند.")
            if(state.inbox.blockedSenders.isEmpty()) Text("فرستنده‌ای مسدود نشده است.")
            state.inbox.blockedSenders.forEach { sender -> key(BankSmsSenders.key(sender)) {
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(sender,style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { vm.unblock(sender) },enabled = !state.busy) { Text("رفع مسدودی") }
                    }
                }
            } }
        }
        return
    }
    StorePage("پیامک بانکی",state.loading,state.error) {
        TextButton(onClick = onBack) { Text("بازگشت") }
        OutlinedButton(onClick = { showBlocked = true },enabled = !state.busy,modifier = Modifier.fillMaxWidth()) {
            Text("فهرست سیاه فرستنده‌ها (" + quantity(state.inbox.blockedSenders.size) + ")")
        }
        FormSection("دریافت پیامک‌های جدید") {
            Text("پیامک بانکی روی همین گوشی بررسی می‌شود. مبلغ، نوع تراکنش و حساب را بازبینی و تأیید می‌کنید، سپس در حسابدار ثبت می‌شود.",style = MaterialTheme.typography.bodyMedium)
            val granted = ContextCompat.checkSelfPermission(context,Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
            if(state.inbox.enabled && granted) FilledTonalButton(onClick = { vm.enable(false) },enabled = !state.busy,modifier = Modifier.fillMaxWidth()) { Text("توقف بررسی خودکار پیامک") }
            else Button(onClick = { disclosure = true },enabled = !state.busy,modifier = Modifier.fillMaxWidth()) { Text("فعال‌سازی دریافت پیامک بانکی") }
            if(Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) TextButton(onClick = { notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) }) { Text("اجازه اعلان پیامک آماده بررسی") }
            permissionMessage?.let { Text(it,style = MaterialTheme.typography.bodySmall) }
        }
        FormSection("ورود دستی پیامک") {
            Entry("متن کامل پیامک بانکی",paste,{ paste = it },lines = 4)
            FilledTonalButton(onClick = { vm.paste(paste) { paste = "" } },enabled = paste.isNotBlank() && !state.busy,modifier = Modifier.fillMaxWidth()) { Text("بررسی متن پیامک") }
        }
        Text("در انتظار تأیید: " + quantity(state.inbox.pending.size),style = MaterialTheme.typography.titleMedium)
        if(state.inbox.pending.isEmpty()) Text("پیامکی برای بررسی وجود ندارد.")
        if(state.inbox.pending.size >= 200) Text("فهرست انتظار پر است؛ پیامک‌های قبلی را بررسی کنید.",color = MaterialTheme.colorScheme.error)
        state.inbox.pending.asReversed().forEach { candidate -> key(candidate.id) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(candidate.sender.ifBlank { "فرستنده نامشخص" },style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { vm.clearError(); reviewingId = candidate.id },enabled = !state.busy) { Text("بررسی") }
                        TextButton(onClick = { vm.clearError(); ignoringId = candidate.id },enabled = !state.busy) { Text("نادیده گرفتن") }
                    }
                }
            }
        } }
    }
    if(disclosure) AlertDialog(onDismissRequest = { disclosure = false },title = { Text("اجازه بررسی پیامک بانکی") },
        text = { Text("با اجازه شما، فقط پیامک‌های جدید دریافتی برای تشخیص عملیات بانکی روی همین گوشی پردازش می‌شوند. تاریخچه پیامک‌ها خوانده نمی‌شود و پیامک ارسال نمی‌کنیم. متن پیام بانکی تا بررسی در حافظه خصوصی برنامه می‌ماند. هر ثبت مالی، تأیید جداگانه شما را لازم دارد. هر زمان می‌توانید این قابلیت را متوقف کنید.") },
        confirmButton = { Button(onClick = { disclosure = false; receiverPermission.launch(Manifest.permission.RECEIVE_SMS) }) { Text("موافقم، درخواست مجوز") } },
        dismissButton = { TextButton(onClick = { disclosure = false }) { Text("انصراف") } })
    state.inbox.pending.firstOrNull { it.id == reviewingId }?.let { candidate ->
        key(candidate.id) { SmsReviewDialog(candidate,state,vm,
            ignore = { reviewingId = null; ignoringId = candidate.id; vm.clearError() },
            dismiss = { reviewingId = null; vm.clearError() }) }
    }
    state.inbox.pending.firstOrNull { it.id == ignoringId }?.let { candidate ->
        val canBlock = candidate.automatic && BankSmsSenders.key(candidate.sender).isNotEmpty()
        AlertDialog(onDismissRequest = { if(!state.busy) ignoringId = null },
            title = { Text("نادیده گرفتن پیامک") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(candidate.sender.ifBlank { "فرستنده نامشخص" })
                Text(if(canBlock) "دیگر پیامک‌های این شماره یا نام فرستنده در فهرست بررسی نیایند؟ فرستنده در فهرست سیاه ذخیره می‌شود و بعداً می‌توانید مسدودی را بردارید." else "این پیامک از فهرست بررسی کنار گذاشته شود؟")
                state.error?.let { Text(it,color = MaterialTheme.colorScheme.error) }
            } },
            confirmButton = { Column {
                if(canBlock) TextButton(onClick = { vm.discard(candidate.id,true) { ignoringId = null } },enabled = !state.busy) { Text("بله، افزودن به فهرست سیاه") }
                TextButton(onClick = { vm.discard(candidate.id,false) { ignoringId = null } },enabled = !state.busy) { Text("فقط همین پیامک") }
            } },
            dismissButton = { TextButton(onClick = { ignoringId = null },enabled = !state.busy) { Text("انصراف") } })
    }
}

@Composable private fun SmsReviewDialog(candidate: BankSmsCandidate, state: BankSmsUiState, vm: BankSmsViewModel, ignore: () -> Unit, dismiss: () -> Unit) {
    var amount by rememberSaveable { mutableStateOf(candidate.parsed.amount.orEmpty()) }
    var currency by rememberSaveable { mutableStateOf(candidate.parsed.currency) }
    var kind by rememberSaveable { mutableStateOf(candidate.parsed.kind) }
    var selectedTags by rememberSaveable { mutableStateOf(listOf<Long>()) }
    var loanId by rememberSaveable { mutableStateOf<Long?>(null) }
    val transactionType = when(kind) {
        BankSmsKind.INCOME -> ir.siamak.fintrack.personalaccountant.data.model.TransactionType.INCOME
        BankSmsKind.EXPENSE -> ir.siamak.fintrack.personalaccountant.data.model.TransactionType.EXPENSE
        BankSmsKind.TRANSFER -> ir.siamak.fintrack.personalaccountant.data.model.TransactionType.TRANSFER
        null -> null
    }
    val allowedTags = state.tags.filter { it.allowedType == transactionType }
    LaunchedEffect(kind) {
        selectedTags = selectedTags.filter { id -> allowedTags.any { it.id == id } }
        loanId = null
    }
    val matching = state.wallets.filter { !it.isCash && BankSmsAccounts.matches(candidate.parsed,it.name,it.cardNumber,it.accountNumber) }
    var newWalletName by rememberSaveable { mutableStateOf(BankSmsAccounts.suggestedName(candidate.parsed)) }
    var balanceOnTarget by rememberSaveable { mutableStateOf(candidate.parsed.incoming == true) }
    var syncBalance by rememberSaveable { mutableStateOf(candidate.parsed.balance != null) }
    var balance by rememberSaveable { mutableStateOf(candidate.parsed.balance.orEmpty()) }
    var balanceCurrency by rememberSaveable { mutableStateOf(candidate.parsed.balanceCurrency) }
    val statementTime = candidate.parsed.occurredAt ?: candidate.receivedAt
    var walletId by rememberSaveable { mutableStateOf(if(kind == BankSmsKind.TRANSFER && candidate.parsed.incoming == true) null else matching.singleOrNull()?.id ?: if(matching.isEmpty() && kind != BankSmsKind.TRANSFER) 0L else null) }
    var toWalletId by rememberSaveable { mutableStateOf(if(kind == BankSmsKind.TRANSFER && candidate.parsed.incoming == true) matching.singleOrNull()?.id ?: if(matching.isEmpty()) 0L else null else null) }
    var memberId by rememberSaveable { mutableStateOf(state.members.singleOrNull()?.id) }
    var date by rememberSaveable { mutableStateOf(Instant.ofEpochMilli(statementTime).atZone(ZoneId.systemDefault()).toLocalDate().toString()) }
    androidx.compose.ui.window.Dialog(onDismissRequest = { if(!state.busy) dismiss() }) {
        Surface(shape = MaterialTheme.shapes.large,modifier = Modifier.fillMaxWidth().fillMaxHeight(.92f)) {
            StorePage("بازبینی تراکنش",error = state.error,bottomBar = {
                Button(onClick = {
                    val originalDay = Instant.ofEpochMilli(statementTime).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                    val time = if(date == originalDay) statementTime else LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    vm.approve(candidate.copy(receivedAt = time),amount,currency,kind,walletId,toWalletId,memberId,if(syncBalance) balance else null,if(syncBalance) balanceCurrency ?: currency else null,newWalletName,balanceOnTarget,selectedTags,loanId,dismiss)
                },enabled = !state.busy && amount.isNotBlank() && currency != null && kind != null && walletId != null && memberId != null && (walletId != 0L || (newWalletName.isNotBlank())) && (!syncBalance || balance.isNotBlank()) && (kind != BankSmsKind.TRANSFER || (toWalletId != null && !(walletId == 0L && toWalletId == 0L) && (toWalletId != 0L || newWalletName.isNotBlank()))),modifier = Modifier.fillMaxWidth()) { Text("تأیید و ثبت در حسابدار") }
                FilledTonalButton(onClick = dismiss,enabled = !state.busy,modifier = Modifier.fillMaxWidth()) { Text("فعلاً نه") }
            }) {
                Text(candidate.body,style = MaterialTheme.typography.bodySmall)
                Entry("مبلغ پیامک *",amount,{ amount = it },number = true)
                ChoiceEntry("واحد مبلغ *",when(currency) { SmsCurrency.RIAL -> "ریال"; SmsCurrency.TOMAN -> "تومان"; null -> "انتخاب واحد" },listOf(SmsCurrency.RIAL to "ریال",SmsCurrency.TOMAN to "تومان")) { currency = it }
                ChoiceEntry("نوع تراکنش",kindLabel(kind),BankSmsKind.entries.map { it to kindLabel(it) }) { kind = it }
                if(transactionType != null) {
                    DataRow("نوع ثبت در درآمد و هزینه",transactionType.toPersianLabel())
                    Text("تگ‌های تراکنش")
                    allowedTags.forEach { tag ->
                        FilterChip(selected = tag.id in selectedTags,onClick = {
                            selectedTags = if(tag.id in selectedTags) selectedTags - tag.id else selectedTags + tag.id
                        },label = { Text(tag.name) })
                    }
                    ir.siamak.fintrack.personalaccountant.presentation.components.LoanTransactionFields(
                        state.loans,transactionType,loanId,allowedTags.filter { it.id in selectedTags }.map { it.name }) { loanId = it }
                }
                if(kind == BankSmsKind.TRANSFER) Text("انتقال بین دو حساب خودتان است؛ برای واریز یا برداشت از شخص دیگر، نوع مربوط را انتخاب کنید. پیامک طرف دیگر همین انتقال را دوباره ثبت نکنید.",style = MaterialTheme.typography.bodySmall)
                ChoiceEntry(if(kind == BankSmsKind.TRANSFER) "حساب مبدأ *" else "حساب *",if(walletId == 0L) "ساخت خودکار حساب جدید" else state.wallets.firstOrNull { it.id == walletId }?.name ?: "انتخاب حساب",state.wallets.map { it.id to it.name } + if(toWalletId != 0L || kind != BankSmsKind.TRANSFER) listOf(0L to "ساخت خودکار حساب جدید") else emptyList()) { walletId = it }
                if(walletId == 0L || (kind == BankSmsKind.TRANSFER && toWalletId == 0L)) Entry("نام حساب جدید *",newWalletName,{ newWalletName = it })
                if(kind == BankSmsKind.TRANSFER) ChoiceEntry("حساب مقصد *",if(toWalletId == 0L) "ساخت خودکار حساب جدید" else state.wallets.firstOrNull { it.id == toWalletId }?.name ?: "انتخاب مقصد",state.wallets.filter { it.id != walletId }.map { it.id to it.name } + if(walletId != 0L) listOf(0L to "ساخت خودکار حساب جدید") else emptyList()) { toWalletId = it }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = syncBalance,onCheckedChange = { syncBalance = it })
                    Text("تطبیق موجودی حساب با مانده پیامک")
                }
                if(syncBalance) {
                    if(kind == BankSmsKind.TRANSFER) ChoiceEntry("مانده مربوط به کدام حساب است؟",if(balanceOnTarget) "حساب مقصد" else "حساب مبدأ",listOf(false to "حساب مبدأ",true to "حساب مقصد")) { balanceOnTarget = it }
                    Entry("مانده پس از تراکنش *",balance,{ balance = it },number = true)
                    ChoiceEntry("واحد مانده *",when(balanceCurrency ?: currency) { SmsCurrency.RIAL -> "ریال"; SmsCurrency.TOMAN -> "تومان"; null -> "انتخاب واحد" },listOf(SmsCurrency.RIAL to "ریال",SmsCurrency.TOMAN to "تومان")) { balanceCurrency = it }
                    Text("پس از تأیید، مانده پیامک مبنای موجودی این حساب است؛ تراکنش‌های جدیدتر حفظ می‌شوند. در انتقال، حساب مربوط به مانده را انتخاب کنید.",style = MaterialTheme.typography.bodySmall)
                }
                ChoiceEntry("شخص *",state.members.firstOrNull { it.id == memberId }?.name ?: "انتخاب شخص",state.members.map { it.id to it.name }) { memberId = it }
                DateEntry("تاریخ تراکنش",date) { date = it }
                if(state.members.isEmpty()) Text("ابتدا یک شخص در اطلاعات پایه ثبت کنید.",color = MaterialTheme.colorScheme.error)
                TextButton(onClick = ignore,enabled = !state.busy) { Text("نادیده‌گرفتن این پیامک") }
            }
        }
    }
}
private fun kindLabel(kind: BankSmsKind?) = when(kind) { null -> "انتخاب نوع تراکنش"; BankSmsKind.INCOME -> "واریز / درآمد"; BankSmsKind.EXPENSE -> "برداشت / هزینه"; BankSmsKind.TRANSFER -> "انتقال بین حساب‌های من" }

private fun ir.siamak.fintrack.personalaccountant.data.model.TransactionType.toPersianLabel() = when(this) {
    ir.siamak.fintrack.personalaccountant.data.model.TransactionType.INCOME -> "درآمد"
    ir.siamak.fintrack.personalaccountant.data.model.TransactionType.EXPENSE -> "هزینه"
    ir.siamak.fintrack.personalaccountant.data.model.TransactionType.TRANSFER -> "انتقال داخلی (خارج از جمع درآمد و هزینه)"
}

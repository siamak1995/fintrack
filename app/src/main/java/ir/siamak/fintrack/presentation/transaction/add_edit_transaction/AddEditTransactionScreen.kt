package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTButton
import ir.siamak.fintrack.presentation.components.FTTextField
import ir.siamak.fintrack.presentation.components.FTTopBar
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.remember

/**
 * صفحه ثبت یا ویرایش تراکنش.
 *
 * @param onBack تابع بازگشت به صفحه قبل
 * @param viewModel ویومدل مربوطه که با Hilt تزریق می‌شود
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    onBack: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    // گوش دادن به رویدادهای یک‌باره مثل نمایش پیام یا موفقیت در ذخیره
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditTransactionViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AddEditTransactionViewModel.UiEvent.SaveSuccess -> {
                    onBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            FTTopBar(
                title = "ثبت تراکنش",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // انتخاب نوع تراکنش (هزینه / درآمد)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { viewModel.onEvent(AddEditTransactionEvent.TypeChanged(TransactionType.EXPENSE)) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("هزینه") }
                SegmentedButton(
                    selected = state.type == TransactionType.INCOME,
                    onClick = { viewModel.onEvent(AddEditTransactionEvent.TypeChanged(TransactionType.INCOME)) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("درآمد") }
            }

            // فیلد مبلغ
            FTTextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(AddEditTransactionEvent.EnteredAmount(it)) },
                label = "مبلغ (تومان)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // نمایش لیست کیف‌پول‌ها (ساده شده برای فاز اول)
            Text(text = "انتخاب حساب:", style = MaterialTheme.typography.labelMedium)
            if (state.wallets.isEmpty()) {
                Text("ابتدا یک حساب بسازید", color = MaterialTheme.colorScheme.error)
            } else {
                // در فازهای بعدی اینجا Dropdown قرار می‌گیرد
                state.wallets.forEach { wallet ->
                    FilterChip(
                        selected = state.selectedWalletId == wallet.id,
                        onClick = { viewModel.onEvent(AddEditTransactionEvent.WalletSelected(wallet.id)) },
                        label = { Text(wallet.name) }
                    )
                }
            }

            // فیلد یادداشت
            FTTextField(
                value = state.note,
                onValueChange = { viewModel.onEvent(AddEditTransactionEvent.EnteredNote(it)) },
                label = "یادداشت (اختیاری)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // دکمه ذخیره
            FTButton(
                text = "ذخیره تراکنش",
                onClick = { viewModel.onEvent(AddEditTransactionEvent.SaveTransaction) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

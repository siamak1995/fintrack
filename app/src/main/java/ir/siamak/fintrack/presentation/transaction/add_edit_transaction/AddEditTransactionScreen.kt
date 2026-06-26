package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTButton
import ir.siamak.fintrack.presentation.components.FTTextField
import ir.siamak.fintrack.presentation.components.FTTopBar
import ir.siamak.fintrack.presentation.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * صفحه ثبت یا ویرایش تراکنش مالی (درآمد/هزینه).
 *
 * @param onBack کالبک برای بازگشت به صفحه قبل (معمولاً داشبورد)
 * @param viewModel ویومدل تزریق شده توسط Hilt
 */
@Composable
fun AddEditTransactionScreen(
    onBack: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    // مدیریت رویدادهای ارسالی از سمت ویومدل
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
                title = "ثبت تراکنش جدید",
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
                .padding(AppTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
        ) {
            // ۱. انتخاب نوع تراکنش
            TransactionTypeSelector(
                selectedType = state.type,
                onTypeSelected = { viewModel.onEvent(AddEditTransactionEvent.TypeChanged(it)) }
            )

            // ۲. ورودی مبلغ
            FTTextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(AddEditTransactionEvent.EnteredAmount(it)) },
                label = "مبلغ (تومان)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // ۳. انتخاب حساب بانکی
            Text(
                text = "انتخاب حساب:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.wallets.isEmpty()) {
                Text(
                    text = "ابتدا یک حساب در بخش 'حساب‌ها' بسازید",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.wallets.forEach { wallet ->
                        FilterChip(
                            selected = state.selectedWalletId == wallet.id,
                            onClick = { viewModel.onEvent(AddEditTransactionEvent.WalletSelected(wallet.id)) },
                            label = { Text(wallet.name) }
                        )
                    }
                }
            }

            // ۴. یادداشت تراکنش
            FTTextField(
                value = state.note,
                onValueChange = { viewModel.onEvent(AddEditTransactionEvent.EnteredNote(it)) },
                label = "یادداشت (اختیاری)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // ۵. دکمه نهایی ذخیره
            FTButton(
                text = "ذخیره تراکنش",
                onClick = { viewModel.onEvent(AddEditTransactionEvent.SaveTransaction) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * کامپوننت انتخابگر نوع تراکنش (هزینه/درآمد) با استفاده از SegmentedButtons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = selectedType == TransactionType.EXPENSE,
            onClick = { onTypeSelected(TransactionType.EXPENSE) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("هزینه")
        }
        SegmentedButton(
            selected = selectedType == TransactionType.INCOME,
            onClick = { onTypeSelected(TransactionType.INCOME) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("درآمد")
        }
    }
}

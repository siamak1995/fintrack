package ir.siamak.fintrack.personalaccountant.presentation.transaction.add_edit_transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.presentation.components.FTButton
import ir.siamak.fintrack.personalaccountant.presentation.components.FTTextField
import ir.siamak.fintrack.personalaccountant.presentation.components.FTTopBar
import ir.siamak.fintrack.personalaccountant.presentation.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * صفحه بازطراحی‌شده ثبت یا ویرایش تراکنش.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    onBack: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditMode = state.currentTransactionId != null

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditTransactionViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                AddEditTransactionViewModel.UiEvent.SaveSuccess -> onBack()
                AddEditTransactionViewModel.UiEvent.DeleteSuccess -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            FTTopBar(
                title = if (isEditMode) "ویرایش تراکنش" else "ثبت تراکنش جدید",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(AppTheme.spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
        ) {
            // ۱. انتخابگر نوع تراکنش
            TransactionTypeSelector(
                selectedType = state.type,
                onTypeSelected = {
                    viewModel.onEvent(AddEditTransactionEvent.TypeChanged(it))
                }
            )

            // ۲. کارت فیلدهای مالی و اصلی
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(AppTheme.spacing.medium)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
                ) {
                    FTTextField(
                        value = state.amount,
                        onValueChange = {
                            viewModel.onEvent(AddEditTransactionEvent.EnteredAmount(it))
                        },
                        label = "مبلغ (تومان)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // فیلد انتخاب حساب مبدا (کشویی)
                    if (state.wallets.isEmpty()) {
                        HintText(text = "ابتدا یک حساب در بخش حساب‌ها بسازید", isError = true)
                    } else {
                        val selectedWalletName = state.wallets.firstOrNull { it.id == state.selectedWalletId }?.name ?: "انتخاب حساب مبدا"
                        var walletDropdownExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = walletDropdownExpanded,
                            onExpandedChange = { walletDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedWalletName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (state.type == TransactionType.TRANSFER) "از حساب (مبدا)" else "انتخاب حساب") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = walletDropdownExpanded,
                                onDismissRequest = { walletDropdownExpanded = false }
                            ) {
                                state.wallets.forEach { wallet ->
                                    DropdownMenuItem(
                                        text = { Text(wallet.name) },
                                        onClick = {
                                            viewModel.onEvent(AddEditTransactionEvent.WalletSelected(wallet.id))
                                            walletDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // فیلد انتخاب حساب مقصد (کشویی - مخصوص حالت انتقال)
                    if (state.type == TransactionType.TRANSFER) {
                        if (state.wallets.isEmpty()) {
                            HintText(text = "حسابی برای انتخاب وجود ندارد", isError = true)
                        } else {
                            val selectedToWalletName = state.wallets.firstOrNull { it.id == state.selectedToWalletId }?.name ?: "انتخاب حساب مقصد"
                            var toWalletDropdownExpanded by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = toWalletDropdownExpanded,
                                onExpandedChange = { toWalletDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedToWalletName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("به حساب (مقصد)") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toWalletDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = toWalletDropdownExpanded,
                                    onDismissRequest = { toWalletDropdownExpanded = false }
                                ) {
                                    state.wallets.forEach { wallet ->
                                        DropdownMenuItem(
                                            text = { Text(wallet.name) },
                                            onClick = {
                                                viewModel.onEvent(AddEditTransactionEvent.ToWalletSelected(wallet.id))
                                                toWalletDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // فیلد انتخاب عضو (کشویی)
                    if (state.members.isEmpty()) {
                        HintText(text = "ابتدا یک عضو در بخش اعضا بسازید", isError = true)
                    } else {
                        val selectedMemberName = state.members.firstOrNull { it.id == state.selectedMemberId }?.name ?: "انتخاب عضو"
                        var memberDropdownExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = memberDropdownExpanded,
                            onExpandedChange = { memberDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedMemberName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("انتخاب عضو") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = memberDropdownExpanded,
                                onDismissRequest = { memberDropdownExpanded = false }
                            ) {
                                state.members.forEach { member ->
                                    DropdownMenuItem(
                                        text = { Text(member.name) },
                                        onClick = {
                                            viewModel.onEvent(AddEditTransactionEvent.MemberSelected(member.id))
                                            memberDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ۳. بخش تگ‌ها (فیلترشده بر اساس نوع تراکنش)
            SectionTitle(text = "تگ‌ها")
            if (state.filteredTags.isEmpty()) {
                HintText(text = "تگ مناسبی برای این نوع تراکنش تعریف نشده است")
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.filteredTags.forEach { tag ->
                        FilterChip(
                            selected = tag.id in state.selectedTagIds,
                            onClick = {
                                viewModel.onEvent(AddEditTransactionEvent.TagToggled(tag.id))
                            },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            if (state.type != TransactionType.TRANSFER) {
                Text(
                    text = "دسته‌بندی خودکار: ${state.selectedCategoryName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // ۴. فیلد یادداشت تراکنش
            FTTextField(
                value = state.note,
                onValueChange = {
                    viewModel.onEvent(AddEditTransactionEvent.EnteredNote(it))
                },
                label = "یادداشت (اختیاری)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // ۵. دکمه‌های عملیات پایین فرم
            FTButton(
                text = if (isEditMode) "ذخیره تغییرات" else "ذخیره تراکنش",
                onClick = {
                    viewModel.onEvent(AddEditTransactionEvent.SaveTransaction)
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (isEditMode) {
                HorizontalDivider()

                OutlinedButton(
                    onClick = {
                        viewModel.onEvent(AddEditTransactionEvent.DeleteTransaction)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "حذف تراکنش",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun HintText(
    text: String,
    isError: Boolean = false
) {
    Text(
        text = text,
        color = if (isError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}


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
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
        ) {
            Text("هزینه")
        }

        SegmentedButton(
            selected = selectedType == TransactionType.INCOME,
            onClick = { onTypeSelected(TransactionType.INCOME) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
        ) {
            Text("درآمد")
        }

        SegmentedButton(
            selected = selectedType == TransactionType.TRANSFER,
            onClick = { onTypeSelected(TransactionType.TRANSFER) },
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
        ) {
            Text("انتقال")
        }
    }
}

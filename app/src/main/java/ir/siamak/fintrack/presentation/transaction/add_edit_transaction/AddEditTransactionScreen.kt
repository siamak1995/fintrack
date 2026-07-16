package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
 * صفحه ثبت یا ویرایش تراکنش.
 *
 * این صفحه فرم کامل تراکنش را نمایش می‌دهد و با توجه به وجود
 * `currentTransactionId` بین حالت ثبت و ویرایش سوییچ می‌کند.
 *
 * ویژگی‌ها:
 * - نمایش عنوان پویا برای ثبت/ویرایش
 * - پشتیبانی از سه نوع تراکنش: هزینه، درآمد، انتقال
 * - نمایش و انتخاب حساب، عضو و تگ
 * - نمایش دکمه حذف فقط در حالت ویرایش
 */
@OptIn(ExperimentalLayoutApi::class)
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
            TransactionTypeSelector(
                selectedType = state.type,
                onTypeSelected = {
                    viewModel.onEvent(AddEditTransactionEvent.TypeChanged(it))
                }
            )

            FTTextField(
                value = state.amount,
                onValueChange = {
                    viewModel.onEvent(AddEditTransactionEvent.EnteredAmount(it))
                },
                label = "مبلغ (تومان)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle(
                text = if (state.type == TransactionType.TRANSFER) {
                    "از حساب (مبدا)"
                } else {
                    "انتخاب حساب"
                }
            )

            if (state.wallets.isEmpty()) {
                HintText(text = "ابتدا یک حساب در بخش حساب‌ها بسازید", isError = true)
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.wallets.forEach { wallet ->
                        FilterChip(
                            selected = state.selectedWalletId == wallet.id,
                            onClick = {
                                viewModel.onEvent(AddEditTransactionEvent.WalletSelected(wallet.id))
                            },
                            label = { Text(wallet.name) }
                        )
                    }
                }
            }

            if (state.type == TransactionType.TRANSFER) {
                SectionTitle(text = "به حساب (مقصد)")

                if (state.wallets.isEmpty()) {
                    HintText(text = "حسابی برای انتخاب وجود ندارد", isError = true)
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.wallets.forEach { wallet ->
                            FilterChip(
                                selected = state.selectedToWalletId == wallet.id,
                                onClick = {
                                    viewModel.onEvent(AddEditTransactionEvent.ToWalletSelected(wallet.id))
                                },
                                label = { Text(wallet.name) }
                            )
                        }
                    }
                }
            }

            SectionTitle(text = "انتخاب عضو")

            if (state.members.isEmpty()) {
                HintText(text = "ابتدا یک عضو در بخش اعضا بسازید", isError = true)
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.members.forEach { member ->
                        FilterChip(
                            selected = state.selectedMemberId == member.id,
                            onClick = {
                                viewModel.onEvent(AddEditTransactionEvent.MemberSelected(member.id))
                            },
                            label = { Text(member.name) }
                        )
                    }
                }
            }

            SectionTitle(text = "تگ‌ها")

            if (state.tags.isEmpty()) {
                HintText(text = "هنوز تگی ساخته نشده است")
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.tags.forEach { tag ->
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
                    text = "دسته‌بندی ذخیره‌شده: ${state.selectedCategoryName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FTTextField(
                value = state.note,
                onValueChange = {
                    viewModel.onEvent(AddEditTransactionEvent.EnteredNote(it))
                },
                label = "یادداشت (اختیاری)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f, fill = true))

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

/**
 * تیتر کوچک هر سکشن فرم.
 */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * متن راهنما یا خطا برای وضعیت‌های خالی.
 */
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
        style = MaterialTheme.typography.bodySmall
    )
}

/**
 * انتخابگر نوع تراکنش.
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

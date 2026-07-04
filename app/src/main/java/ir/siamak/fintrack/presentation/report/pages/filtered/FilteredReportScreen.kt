package ir.siamak.fintrack.presentation.report.pages.filtered

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.core.datepicker.components.PersianDateRangePickerDialog
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.state.rememberPersianDateRangePickerState
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.report.model.FilteredTransactionResult
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredReportScreen(
    uiState: FilteredReportUiState,
    onBackClick: () -> Unit,
    onMemberSelected: (Long?) -> Unit,
    onWalletSelected: (Long?) -> Unit,
    onTypeSelected: (TransactionType?) -> Unit,
    onSelectDateRangeClick: () -> Unit,
    onClearDateRangeClick: () -> Unit,
    onResetFiltersClick: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onConfirmDateRange: (PersianDate?, PersianDate?) -> Unit
) {
    val datePickerState = rememberPersianDateRangePickerState(
        initialRange = PersianDateRange(
            start = uiState.selectedFromDate,
            end = uiState.selectedToDate
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "گزارش پیشرفته ترکیبی",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "جستجوی دقیق با ترکیب فیلترها",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(text = "بازگشت")
                    }
                },
                actions = {
                    IconButton(onClick = onResetFiltersClick) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "ریست فیلترها")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ۱. سکشن بالایی برای فیلترها
                FilterSelectionPanel(
                    uiState = uiState,
                    onMemberSelected = onMemberSelected,
                    onWalletSelected = onWalletSelected,
                    onTypeSelected = onTypeSelected,
                    onSelectDateRangeClick = onSelectDateRangeClick,
                    onClearDateRangeClick = onClearDateRangeClick
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ۲. بدنه اصلی حاوی نتایج
                if (uiState.isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.errorMessage != null) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            val totalSum = uiState.transactions.sumOf { it.signedAmount }

                            SummaryCard(
                                count = uiState.transactions.size,
                                totalSum = totalSum
                            )
                        }

                        if (uiState.transactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "هیچ تراکنشی یافت نشد", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            items(
                                items = uiState.transactions,
                                key = { it.id }
                            ) { item ->
                                TransactionResultItem(item = item)
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }

            if (uiState.isDatePickerVisible) {
                PersianDateRangePickerDialog(
                    visible = true,
                    state = datePickerState,
                    onDismissRequest = onDismissDatePicker,
                    onConfirmClick = {
                        val start = datePickerState.selectedRange.start
                        val end = datePickerState.selectedRange.end
                        onConfirmDateRange(start, end)
                    }
                )
            }
        }
    }
}

@Composable
fun ColumnScope.fillWeight(weight: Float): Modifier = Modifier.weight(weight)

@Composable
private fun FilterSelectionPanel(
    uiState: FilteredReportUiState,
    onMemberSelected: (Long?) -> Unit,
    onWalletSelected: (Long?) -> Unit,
    onTypeSelected: (TransactionType?) -> Unit,
    onSelectDateRangeClick: () -> Unit,
    onClearDateRangeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ردیف اول فیلترها (عضو و حساب)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // منوی انتخاب عضو
                var memberExpanded by remember { mutableStateOf(false) }
                val selectedMemberName = uiState.members.find { it.id == uiState.selectedMemberId }?.name ?: "همه اعضا"

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { memberExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = selectedMemberName, maxLines = 1)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = memberExpanded,
                        onDismissRequest = { memberExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("همه اعضا") },
                            onClick = {
                                onMemberSelected(null)
                                memberExpanded = false
                            }
                        )
                        uiState.members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member.name) },
                                onClick = {
                                    onMemberSelected(member.id)
                                    memberExpanded = false
                                }
                            )
                        }
                    }
                }

                // منوی انتخاب کیف پول / حساب
                var walletExpanded by remember { mutableStateOf(false) }
                val selectedWalletName = uiState.wallets.find { it.id == uiState.selectedWalletId }?.name ?: "همه حساب‌ها"

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { walletExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = selectedWalletName, maxLines = 1)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = walletExpanded,
                        onDismissRequest = { walletExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("همه حساب‌ها") },
                            onClick = {
                                onWalletSelected(null)
                                walletExpanded = false
                            }
                        )
                        uiState.wallets.forEach { wallet ->
                            DropdownMenuItem(
                                text = { Text(wallet.name) },
                                onClick = {
                                    onWalletSelected(wallet.id)
                                    walletExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ردیف دوم فیلترها (نوع تراکنش و بازه تاریخ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // منوی انتخاب نوع
                var typeExpanded by remember { mutableStateOf(false) }
                val selectedTypeName = when(uiState.selectedType) {
                    TransactionType.INCOME -> "فقط واریز"
                    TransactionType.EXPENSE -> "فقط برداشت/هزینه"
                    TransactionType.TRANSFER -> "فقط انتقال"
                    null -> "همه تراکنش‌ها"
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { typeExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = selectedTypeName, maxLines = 1)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("همه تراکنش‌ها") },
                            onClick = {
                                onTypeSelected(null)
                                typeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("واریز (درآمد)") },
                            onClick = {
                                onTypeSelected(TransactionType.INCOME)
                                typeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("هزینه / برداشت") },
                            onClick = {
                                onTypeSelected(TransactionType.EXPENSE)
                                typeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("انتقال") },
                            onClick = {
                                onTypeSelected(TransactionType.TRANSFER)
                                typeExpanded = false
                            }
                        )
                    }
                }

                // دکمه بازه تاریخ
                val hasDateFilter = uiState.selectedFromDate != null || uiState.selectedToDate != null
                OutlinedButton(
                    onClick = onSelectDateRangeClick,
                    modifier = Modifier.weight(1f),
                    colors = if (hasDateFilter) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(
                        text = if (hasDateFilter) "تاریخ فیلتر شده" else "انتخاب تاریخ",
                        color = if (hasDateFilter) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                    if (hasDateFilter) {
                        IconButton(
                            onClick = {
                                onClearDateRangeClick()
                            },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(count: Int, totalSum: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "تعداد کل نتایج", style = MaterialTheme.typography.labelMedium)
                Text(text = "$count تراکنش", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "عملکرد تراز کل بازه", style = MaterialTheme.typography.labelMedium)
                val sumColor = if (totalSum >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                Text(
                    text = "${if (totalSum > 0) "+" else ""}${totalSum.toAmountText()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = sumColor
                )
            }
        }
    }
}

@Composable
fun TransactionResultItem(
    item: FilteredReportUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ردیف اول: عنوان دسته بندی/تراکنش و مبلغ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.amountText,
                    style = MaterialTheme.typography.titleMedium,
                    color = item.amountColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // توضیحات (در صورت وجود)
            if (!item.note.isNullOrBlank()) {
                Text(
                    text = item.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // ردیف دوم: اطلاعات برچسب‌ها (کیف پول، عضو، تاریخ و نوع تراکنش)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // بخش چپ: کیف پول و عضو
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // برچسب کیف پول
                    Surface(
                        color = item.walletColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.walletName,
                            color = item.walletColor,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // برچسب عضو
                    Surface(
                        color = item.memberColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.memberName,
                            color = item.memberColor,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // بخش راست: تاریخ و نوع تراکنش
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // برچسب نوع تراکنش (واریز/برداشت/انتقال)
                    Surface(
                        color = item.typeColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = item.typeLabel,
                            color = item.typeColor,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun LabelIndicator(name: String, colorHex: String) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.outline
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(8.dp).background(color = parsedColor, shape = CircleShape))
        Text(text = name, style = MaterialTheme.typography.labelSmall)
    }
}

private fun Long.toPersianDateString(): String {
    val localDate = java.time.Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    val persianDate = JalaliDateConverter.fromGregorian(localDate)
    return "${persianDate.year}/${persianDate.month}/${persianDate.day}"
}

private fun Long.toAmountText(): String = "%,d تومان".format(this)

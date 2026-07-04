package ir.siamak.fintrack.presentation.report.pages.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.components.PersianDateRangePickerDialog
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.state.rememberPersianDateRangePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletReportScreen(
    uiState: WalletReportUiState,
    onBackClick: () -> Unit,
    onSelectDateRangeClick: () -> Unit,
    onClearDateRangeClick: () -> Unit,
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
                            text = "گزارش کیف پول‌ها",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "وضعیت مالی و تراکنش‌های حساب‌ها",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = uiState.errorMessage, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        item {
                            DateRangeFilterCard(
                                fromDate = uiState.selectedFromDate,
                                toDate = uiState.selectedToDate,
                                onSelectDateRangeClick = onSelectDateRangeClick,
                                onClearDateRangeClick = onClearDateRangeClick
                            )
                        }

                        item {
                            Text(
                                text = "لیست گزارش حساب‌ها",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (uiState.items.isEmpty()) {
                            item {
                                EmptyContent()
                            }
                        } else {
                            items(
                                items = uiState.items,
                                key = { it.walletId }
                            ) { item ->
                                WalletReportCard(item = item)
                            }
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
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
private fun DateRangeFilterCard(
    fromDate: PersianDate?,
    toDate: PersianDate?,
    onSelectDateRangeClick: () -> Unit,
    onClearDateRangeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "فیلتر بازه زمانی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = buildDateRangeText(fromDate, toDate),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onSelectDateRangeClick) {
                    Text(text = "انتخاب بازه")
                }

                if (fromDate != null || toDate != null) {
                    FilterChip(
                        selected = true,
                        onClick = onClearDateRangeClick,
                        label = { Text("حذف فیلتر") },
                        colors = FilterChipDefaults.filterChipColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletReportCard(item: WalletReportUiModel) {
    val balancePresentation = item.toBalancePresentation()
    val walletHexColor = try {
        Color(android.graphics.Color.parseColor(item.walletColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(color = walletHexColor, shape = CircleShape)
                )

                Text(
                    text = item.walletName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            ReportRow(
                title = "موجودی کل فعلی",
                value = item.currentBalance.toAmountText()
            )

            ReportRow(
                title = "مجموع واریز (بازه)",
                value = item.totalIncome.toAmountText()
            )

            ReportRow(
                title = "مجموع برداشت (بازه)",
                value = item.totalExpense.toAmountText()
            )

            ReportRow(
                title = balancePresentation.title,
                value = balancePresentation.value.toAmountText(),
                valueColor = balancePresentation.color
            )

            ReportRow(
                title = "تعداد تراکنش‌ها",
                value = item.transactionCount.toString()
            )
        }
    }
}

@Composable
private fun ReportRow(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun EmptyContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "تراکنشی برای کیف پول‌ها ثبت نشده است",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private data class BalancePresentation(
    val title: String,
    val value: Long,
    val color: Color
)

@Composable
private fun WalletReportUiModel.toBalancePresentation(): BalancePresentation {
    return when {
        totalIncome > totalExpense -> BalancePresentation(
            title = "وضعیت کل (سودده)",
            value = totalIncome - totalExpense,
            color = Color(0xFF2E7D32) // سبز تیره
        )
        totalExpense > totalIncome -> BalancePresentation(
            title = "وضعیت کل (ضررده)",
            value = totalExpense - totalIncome,
            color = Color(0xFFC62828) // قرمز تیره
        )
        else -> BalancePresentation(
            title = "وضعیت کل (بدون تغییر تراز)",
            value = 0L,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun buildDateRangeText(fromDate: PersianDate?, toDate: PersianDate?): String {
    return when {
        fromDate != null && toDate != null -> "${fromDate.toDisplayText()} تا ${toDate.toDisplayText()}"
        fromDate != null -> "از ${fromDate.toDisplayText()} تا امروز"
        toDate != null -> "از ابتدا تا ${toDate.toDisplayText()}"
        else -> "از ابتدای داده‌ها تا امروز"
    }
}

private fun PersianDate.toDisplayText(): String = "$year/$month/$day"

private fun Long.toAmountText(): String = "%,d تومان".format(this)

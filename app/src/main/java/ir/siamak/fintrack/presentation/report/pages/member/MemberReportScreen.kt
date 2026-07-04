package ir.siamak.fintrack.presentation.report.pages.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.model.PersianDate

/**
 * Member report screen.
 *
 * این صفحه گزارش اعضا را نمایش می‌دهد.
 * بازه زمانی اختیاری است و اگر توسط کاربر انتخاب نشود،
 * سیستم باید گزارش را از ابتدای داده‌ها تا امروز محاسبه کند.
 *
 * @param uiState وضعیت صفحه
 * @param onBackClick بازگشت
 * @param onSelectDateRangeClick باز کردن انتخاب‌گر بازه تاریخ
 * @param onClearDateRangeClick حذف فیلتر تاریخ
 * @param onDismissDatePicker بستن انتخاب‌گر
 * @param onConfirmDateRange تایید بازه انتخابی
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberReportScreen(
    uiState: MemberReportUiState,
    onBackClick: () -> Unit,
    onSelectDateRangeClick: () -> Unit,
    onClearDateRangeClick: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onConfirmDateRange: (PersianDate?, PersianDate?) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "گزارش اعضا")
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(text = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
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
                    CircularLoading()
                }

                uiState.errorMessage != null -> {
                    ErrorContent(message = uiState.errorMessage)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            DateRangeFilterCard(
                                fromDate = uiState.selectedFromDate,
                                toDate = uiState.selectedToDate,
                                onSelectDateRangeClick = onSelectDateRangeClick,
                                onClearDateRangeClick = onClearDateRangeClick
                            )
                        }

                        item {
                            Text(
                                text = "گزارش درآمد و هزینه به تفکیک اعضا",
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
                                key = { it.memberId }
                            ) { item ->
                                MemberReportCard(item = item)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }

            if (uiState.isDatePickerVisible) {
                /**
                 * اینجا PersianDateRangePicker واقعی پروژه را قرار بده.
                 *
                 * مثال:
                 *
                 * PersianDateRangePickerDialog(
                 *     initialFromDate = uiState.selectedFromDate,
                 *     initialToDate = uiState.selectedToDate,
                 *     onDismiss = onDismissDatePicker,
                 *     onConfirm = { from, to ->
                 *         onConfirmDateRange(from, to)
                 *     }
                 * )
                 */
            }
        }
    }
}

@Composable
private fun DateRangeFilterCard(
    fromDate: PersianDate?,
    toDate: PersianDate?,
    onSelectDateRangeClick: () -> Unit,
    onClearDateRangeClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "بازه تاریخ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = buildDateRangeText(fromDate, toDate),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onSelectDateRangeClick) {
                    Text(text = "انتخاب بازه")
                }

                if (fromDate != null || toDate != null) {
                    TextButton(onClick = onClearDateRangeClick) {
                        Text(text = "حذف فیلتر")
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberReportCard(
    item: MemberReportUiModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = item.memberName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            ReportRow(
                title = "درآمد",
                value = item.totalIncome.toAmountText()
            )

            ReportRow(
                title = "هزینه",
                value = item.totalExpense.toAmountText()
            )

            ReportRow(
                title = "خالص",
                value = item.balance.toAmountText()
            )

            ReportRow(
                title = "تعداد تراکنش",
                value = item.transactionCount.toString()
            )
        }
    }
}

@Composable
private fun ReportRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CircularLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EmptyContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اطلاعاتی برای این بازه زمانی وجود ندارد",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun buildDateRangeText(
    fromDate: PersianDate?,
    toDate: PersianDate?,
): String {
    return if (fromDate != null && toDate != null) {
        "${fromDate.toDisplayText()} تا ${toDate.toDisplayText()}"
    } else {
        "از ابتدای داده‌ها تا امروز"
    }
}

private fun PersianDate.toDisplayText(): String {
    return "$year/$month/$day"
}

private fun Long.toAmountText(): String {
    return "%,d تومان".format(this)
}

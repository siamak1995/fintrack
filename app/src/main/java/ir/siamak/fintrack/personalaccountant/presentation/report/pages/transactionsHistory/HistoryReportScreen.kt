package ir.siamak.fintrack.personalaccountant.presentation.report.pages.transactionsHistory

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.roundToInt
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.components.PersianDateRangePickerDialog
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.common.core.datepicker.state.rememberPersianDateRangePickerState

/**
 * صفحه گزارش تاریخچه تراکنش‌ها.
 *
 * قابلیت‌ها:
 * - فیلتر بر اساس نوع تراکنش: درآمد، هزینه، انتقال
 * - فیلتر چندانتخابی بر اساس تگ‌ها
 * - فیلتر بازه زمانی
 * - نمایش کارت خلاصه متناسب با فیلترهای فعال
 * - نمایش نمودار ستونی بر اساس حساب/بانک
 * - نمایش ریز تراکنش‌ها در کارت‌های جدا
 *
 * @param uiState وضعیت فعلی صفحه
 * @param onBackClick بازگشت به صفحه قبل
 * @param onSelectDateRangeClick نمایش انتخاب‌گر تاریخ
 * @param onClearDateRangeClick حذف فیلتر بازه زمانی
 * @param onDismissDatePicker بستن انتخاب‌گر تاریخ
 * @param onConfirmDateRange تایید بازه انتخاب‌شده
 * @param onTypeSelected انتخاب نوع تراکنش
 * @param onTagToggle انتخاب یا حذف تگ از فیلتر
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryReportScreen(
    uiState: HistoryReportUiState,
    onBackClick: () -> Unit,
    onShowDatePicker: () -> Unit,
    onHideDatePicker: () -> Unit,
    onClearDateRange: () -> Unit,
    onDateRangeSelected: (PersianDate?, PersianDate?) -> Unit,
    onTypeSelected: (HistoryTransactionTypeFilter) -> Unit,
    onTagClick: (Long) -> Unit
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
                            text = "گزارش تاریخچه تراکنش‌ها",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "فیلتر و بررسی کامل درآمد، هزینه و انتقال",
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyLarge
                        )
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
                            HistoryDateRangeFilterCard(
                                fromDate = uiState.selectedFromDate,
                                toDate = uiState.selectedToDate,
                                onSelectDateRangeClick = onShowDatePicker,
                                onClearDateRangeClick = onClearDateRange
                            )
                        }

                        item {
                            TransactionTypeFilterSection(
                                selectedType = uiState.selectedType,
                                onTypeSelected = onTypeSelected
                            )
                        }

                        item {
                            TagFilterSection(
                                tags = uiState.availableTags,
                                selectedTagIds = uiState.selectedTagIds,
                                onTagToggle = onTagClick
                            )
                        }

                        item {
                            HistorySummaryCard(
                                summary = uiState.summary,
                                selectedType = uiState.selectedType,
                                hasTagFilter = uiState.selectedTagIds.isNotEmpty(),
                                hasDateFilter = uiState.selectedFromDate != null || uiState.selectedToDate != null
                            )
                        }

                        item {
                            HistoryChartCard(
                                chartItems = uiState.chartItems,
                                selectedType = uiState.selectedType
                            )
                        }

                        item {
                            Text(
                                text = "ریز تراکنش‌ها",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (uiState.transactions.isEmpty()) {
                            item { EmptyHistoryContent() }
                        } else {
                            items(
                                items = uiState.transactions,
                                key = { it.id }
                            ) { item ->
                                HistoryTransactionCard(item = item)
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
                    onDismissRequest = onHideDatePicker,
                    onConfirmClick = {
                        onDateRangeSelected(
                            datePickerState.selectedRange.start,
                            datePickerState.selectedRange.end
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun HistoryDateRangeFilterCard(
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
private fun TransactionTypeFilterSection(
    selectedType: HistoryTransactionTypeFilter,
    onTypeSelected: (HistoryTransactionTypeFilter) -> Unit
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
                text = "نوع تراکنش",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HistoryTransactionTypeFilter.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { onTypeSelected(type) },
                        label = { Text(type.toDisplayText()) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagFilterSection(
    tags: List<HistoryTagUiModel>,
    selectedTagIds: Set<Long>,
    onTagToggle: (Long) -> Unit
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
                text = "فیلتر تگ‌ها",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (tags.isEmpty()) {
                Text(
                    text = "تگی برای فیلتر وجود ندارد",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        FilterChip(
                            selected = tag.id in selectedTagIds,
                            onClick = { onTagToggle(tag.id) },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySummaryCard(
    summary: HistorySummaryUiState,
    selectedType: HistoryTransactionTypeFilter,
    hasTagFilter: Boolean,
    hasDateFilter: Boolean
) {
    val showCompactSingleValue =
        selectedType != HistoryTransactionTypeFilter.ALL && !hasTagFilter && !hasDateFilter

    val incomeColor = Color(0xFF2E7D32)
    val expenseColor = Color(0xFFC62828)
    val transferColor = Color(0xFF1565C0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "خلاصه گزارش",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (showCompactSingleValue) {
                val title = when (selectedType) {
                    HistoryTransactionTypeFilter.INCOME -> "مجموع درآمد"
                    HistoryTransactionTypeFilter.EXPENSE -> "مجموع هزینه"
                    HistoryTransactionTypeFilter.TRANSFER -> "مجموع انتقال"
                    HistoryTransactionTypeFilter.ALL -> "جمع کل"
                }

                val value = when (selectedType) {
                    HistoryTransactionTypeFilter.INCOME -> summary.totalIncome
                    HistoryTransactionTypeFilter.EXPENSE -> summary.totalExpense
                    HistoryTransactionTypeFilter.TRANSFER -> summary.totalTransfer
                    HistoryTransactionTypeFilter.ALL -> summary.netBalance
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = value.toAmountText(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                SummaryRow(
                    title = "مجموع درآمد",
                    value = summary.totalIncome.toAmountText(),
                    valueColor = incomeColor
                )
                SummaryRow(
                    title = "مجموع هزینه",
                    value = summary.totalExpense.toAmountText(),
                    valueColor = expenseColor
                )
                SummaryRow(
                    title = "مجموع انتقال",
                    value = summary.totalTransfer.toAmountText(),
                    valueColor = transferColor
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                SummaryRow(
                    title = "خالص تراز",
                    value = summary.netBalance.toAmountText(),
                    valueColor = if (summary.netBalance >= 0) incomeColor else expenseColor
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun HistoryChartCard(
    chartItems: List<HistoryChartItemUiModel>,
    selectedType: HistoryTransactionTypeFilter
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "سهم حساب‌ها / بانک‌ها",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = when (selectedType) {
                    HistoryTransactionTypeFilter.INCOME -> "درصد توزیع درآمد بین حساب‌ها"
                    HistoryTransactionTypeFilter.EXPENSE -> "درصد توزیع هزینه بین حساب‌ها"
                    HistoryTransactionTypeFilter.TRANSFER -> "درصد توزیع انتقال بین حساب‌ها"
                    HistoryTransactionTypeFilter.ALL -> "درصد توزیع کل تراکنش‌ها بین حساب‌ها"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (chartItems.isEmpty()) {
                Text(
                    text = "داده‌ای برای نمایش نمودار وجود ندارد",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                HistoryPieChart(chartItems = chartItems)
            }
        }
    }
}


@Composable
private fun HistoryBarChart(
    chartItems: List<HistoryChartItemUiModel>
) {
    val maxValue = chartItems.maxOfOrNull { it.value } ?: 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        chartItems.forEach { item ->
            val animatedHeight by animateFloatAsState(
                targetValue = (item.value / maxValue).coerceIn(0f, 1f),
                animationSpec = tween(durationMillis = 900),
                label = "history_bar_chart"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .fillMaxHeight(animatedHeight)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = item.value.toLong().toAmountTextShort(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HistoryTransactionCard(
    item: HistoryTransactionUiModel
) {
    val amountColor = when (item.type) {
        HistoryTransactionTypeFilter.INCOME -> Color(0xFF2E7D32)
        HistoryTransactionTypeFilter.EXPENSE -> Color(0xFFC62828)
        HistoryTransactionTypeFilter.TRANSFER -> Color(0xFF1565C0)
        HistoryTransactionTypeFilter.ALL -> MaterialTheme.colorScheme.onSurface
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = item.walletName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = item.amount.toAmountText(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )
            }

            item.categoryName?.let { category ->
                Text(
                    text = "دسته‌بندی: $category",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item.note?.takeIf { it.isNotBlank() }?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.tags.forEach { tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = tag.color.toComposeColorOrDefault(),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = item.dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


private fun Long?.toComposeColorOrDefault(
    default: Color = Color(0xFF757575)
): Color {
    return this?.let(::Color) ?: default
}

@Composable
private fun EmptyHistoryContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "تراکنشی مطابق فیلترهای انتخابی پیدا نشد",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun buildDateRangeText(fromDate: PersianDate?, toDate: PersianDate?): String {
    return when {
        fromDate != null && toDate != null -> "${fromDate.toDisplayText()} تا ${toDate.toDisplayText()}"
        fromDate != null -> "از ${fromDate.toDisplayText()} تا امروز"
        toDate != null -> "از ابتدای داده‌ها تا ${toDate.toDisplayText()}"
        else -> "از ابتدای داده‌ها تا امروز"
    }
}

private fun PersianDate.toDisplayText(): String = "$year/$month/$day"

private fun Long.toAmountText(): String = "%,d تومان".format(this)

private fun Long.toAmountTextShort(): String {
    return when {
        this >= 1_000_000 -> "${this / 1_000_000}م"
        this >= 1_000 -> "${this / 1_000}ه"
        else -> toString()
    }
}

private fun HistoryTransactionTypeFilter.toDisplayText(): String {
    return when (this) {
        HistoryTransactionTypeFilter.ALL -> "همه"
        HistoryTransactionTypeFilter.INCOME -> "درآمد"
        HistoryTransactionTypeFilter.EXPENSE -> "هزینه"
        HistoryTransactionTypeFilter.TRANSFER -> "انتقال"
    }
}

private fun String.toComposeColorOrDefault(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (_: Exception) {
        Color(0xFF757575)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HistoryPieChart(
    chartItems: List<HistoryChartItemUiModel>
) {
    val total = chartItems.sumOf { it.value.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f

    val chartColors = listOf(
        Color(0xFF4F46E5),
        Color(0xFF059669),
        Color(0xFFDC2626),
        Color(0xFFD97706),
        Color(0xFF0891B2),
        Color(0xFF7C3AED),
        Color(0xFF65A30D),
        Color(0xFFDB2777)
    )

    val segments = chartItems.mapIndexed { index, item ->
        HistoryPieSegmentUiModel(
            label = item.label,
            value = item.value,
            percentage = (item.value / total) * 100f,
            color = chartColors[index % chartColors.size]
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .aspectRatio(1f)
            ) {
                val strokeWidth = size.minDimension * 0.16f
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                var startAngle = -90f
                segments.forEach { segment ->
                    val sweepAngle = (segment.percentage / 100f) * 360f
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "کل",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = total.toLong().toAmountTextShort(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            segments.forEach { segment ->
                HistoryChartLegendItem(segment = segment)
            }
        }
    }
}

data class HistoryPieSegmentUiModel(
    val label: String,
    val value: Float,
    val percentage: Float,
    val color: Color
)

@Composable
private fun HistoryChartLegendItem(
    segment: HistoryPieSegmentUiModel
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(segment.color, CircleShape)
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = segment.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${segment.percentage.roundToInt()}٪ - ${segment.value.toLong().toAmountTextShort()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}




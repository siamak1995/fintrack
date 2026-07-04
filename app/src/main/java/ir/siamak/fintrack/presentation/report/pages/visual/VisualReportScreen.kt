package ir.siamak.fintrack.presentation.report.pages.visual

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.core.datepicker.components.PersianDateRangePickerDialog
import ir.siamak.fintrack.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.core.datepicker.state.rememberPersianDateRangePickerState
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualReportScreen(
    onBackClick: () -> Unit
) {
    var showDatePickerRange by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberPersianDateRangePickerState()

    var confirmedRange by remember { mutableStateOf(PersianDateRange()) }
    var previousPeriodText by remember { mutableStateOf("بازه قبلی مشخص نیست") }
    var daysDifference by remember { mutableStateOf(0L) }

    val currentPeriodIncome = remember { listOf(140f, 190f, 110f, 250f, 180f) }
    val currentPeriodExpense = remember { listOf(90f, 120f, 80f, 150f, 110f) }
    val previousPeriodIncome = remember { listOf(110f, 160f, 95f, 200f, 140f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("گزارش تصویری و مقایسه‌ای") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FTCard(onClick = { showDatePickerRange = true }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            val dateLabel = if (!confirmedRange.isEmpty) {
                                PersianCalendarFormatter.formatRange(confirmedRange)
                            } else {
                                "انتخاب بازه زمانی"
                            }

                            Text(
                                text = dateLabel,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = "تغییر بازه",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                FTCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("روند درآمد و هزینه در این بازه")
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomBarChart(
                            incomeData = currentPeriodIncome,
                            expenseData = currentPeriodExpense,
                            labels = listOf("بخش ۱", "بخش ۲", "بخش ۳", "بخش ۴", "بخش ۵")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendItem("درآمد", Color(0xFF22C55E))
                            LegendItem("هزینه", Color(0xFFEF4444))
                        }
                    }
                }
            }

            item {
                FTCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مقایسه درآمد با دوره مشابه قبلی",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (daysDifference > 0) {
                                "مقایسه بر اساس $daysDifference روز قبل از بازه جاری:\n($previousPeriodText)"
                            } else {
                                "برای مقایسه، بازه تاریخ را مشخص کنید."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ComparisonChart(
                            currentValues = currentPeriodIncome,
                            previousValues = previousPeriodIncome
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendItem("دوره فعلی", MaterialTheme.colorScheme.primary)
                            LegendItem("دوره مشابه قبلی", Color.LightGray)
                        }
                    }
                }
            }
        }

        PersianDateRangePickerDialog(
            visible = showDatePickerRange,
            state = dateRangePickerState,
            onDismissRequest = { showDatePickerRange = false },
            onConfirmClick = {
                val range = dateRangePickerState.selectedRange
                confirmedRange = range

                val start = range.start
                val end = range.end

                if (start != null && end != null) {
                    val startLocalDate = JalaliDateConverter.toGregorian(start)
                    val endLocalDate = JalaliDateConverter.toGregorian(end)

                    daysDifference =
                        ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1L

                    val previousStartLocalDate = startLocalDate.minusDays(daysDifference)
                    val previousEndLocalDate = startLocalDate.minusDays(1)

                    val previousStartPersian =
                        JalaliDateConverter.fromGregorian(previousStartLocalDate)
                    val previousEndPersian =
                        JalaliDateConverter.fromGregorian(previousEndLocalDate)

                    previousPeriodText = PersianCalendarFormatter.formatRange(
                        PersianDateRange(
                            start = previousStartPersian,
                            end = previousEndPersian
                        )
                    )
                } else {
                    daysDifference = 0L
                    previousPeriodText = "بازه قبلی مشخص نیست"
                }

                showDatePickerRange = false
            }
        )
    }
}

@Composable
fun CustomBarChart(
    incomeData: List<Float>,
    expenseData: List<Float>,
    labels: List<String>
) {
    val maxValue = (incomeData + expenseData).maxOrNull() ?: 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        labels.forEachIndexed { index, label ->
            val animatedIncomeHeight by animateFloatAsState(
                targetValue = incomeData.getOrElse(index) { 0f } / maxValue,
                animationSpec = tween(durationMillis = 900),
                label = "income_bar"
            )
            val animatedExpenseHeight by animateFloatAsState(
                targetValue = expenseData.getOrElse(index) { 0f } / maxValue,
                animationSpec = tween(durationMillis = 900),
                label = "expense_bar"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .fillMaxHeight(animatedIncomeHeight.coerceIn(0f, 1f))
                            .background(
                                color = Color(0xFF22C55E),
                                shape = RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .fillMaxHeight(animatedExpenseHeight.coerceIn(0f, 1f))
                            .background(
                                color = Color(0xFFEF4444),
                                shape = RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = label, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ComparisonChart(
    currentValues: List<Float>,
    previousValues: List<Float>
) {
    val maxValue = (currentValues + previousValues).maxOrNull() ?: 1f
    val itemCount = maxOf(currentValues.size, previousValues.size)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(itemCount) { index ->
            val currentValue = currentValues.getOrElse(index) { 0f }
            val previousValue = previousValues.getOrElse(index) { 0f }

            val animatedCurrentHeight by animateFloatAsState(
                targetValue = currentValue / maxValue,
                animationSpec = tween(durationMillis = 900),
                label = "current_compare_bar"
            )
            val animatedPreviousHeight by animateFloatAsState(
                targetValue = previousValue / maxValue,
                animationSpec = tween(durationMillis = 900),
                label = "previous_compare_bar"
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .fillMaxHeight(animatedPreviousHeight.coerceIn(0f, 1f))
                        .background(
                            color = Color.LightGray.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .fillMaxHeight(animatedCurrentHeight.coerceIn(0f, 1f))
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun LegendItem(
    text: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

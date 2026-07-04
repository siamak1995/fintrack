package ir.siamak.fintrack.presentation.report.pages.visual

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualReportScreen(
    onBackClick: () -> Unit
) {
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var previousPeriodText by remember { mutableStateOf("بازه قبلی مشخص نیست") }
    var daysDifference by remember { mutableStateOf(0L) }

    var showDatePickerRange by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    // مقادیر فرضی برای نمایش نمودارها
    val currentPeriodIncome = listOf(140f, 190f, 110f, 250f, 180f)
    val currentPeriodExpense = listOf(90f, 120f, 80f, 150f, 110f)
    val previousPeriodIncome = listOf(110f, 160f, 95f, 200f, 140f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("گزارش تصویری و مقایسه‌ای") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // بخش ۱: انتخاب بازه تاریخ استاندارد
            item {
                FTCard(onClick = { showDatePickerRange = true }) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            val dateLabel = if (startDateText.isNotEmpty() && endDateText.isNotEmpty()) {
                                "از $startDateText تا $endDateText"
                            } else {
                                "انتخاب بازه زمانی"
                            }
                            Text(text = dateLabel, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            "تغییر بازه",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // بخش ۲: نمودار اصلی درآمد و هزینه
            item {
                FTCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("روند درآمد و هزینه در این بازه")
                        Spacer(Modifier.height(16.dp))

                        CustomBarChart(
                            incomeData = currentPeriodIncome,
                            expenseData = currentPeriodExpense,
                            labels = listOf("بخش ۱", "بخش ۲", "بخش ۳", "بخش ۴", "بخش ۵")
                        )

                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendItem("درآمد", Color(0xFF22C55E))
                            LegendItem("هزینه", Color(0xFFEF4444))
                        }
                    }
                }
            }

            // بخش ۳: نمودار مقایسه‌ای با دوره مشابه قبلی
            item {
                FTCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CompareArrows, null, tint = Color(0xFF8B5CF6))
                            Spacer(Modifier.width(8.dp))
                            Text("مقایسه درآمد با دوره مشابه قبلی", fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
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

                        Spacer(Modifier.height(24.dp))

                        ComparisonChart(
                            currentValues = currentPeriodIncome,
                            previousValues = previousPeriodIncome
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendItem("دوره فعلی", MaterialTheme.colorScheme.primary)
                            LegendItem("دوره مشابه قبلی", Color.LightGray)
                        }
                    }
                }
            }
        }

        // دیالوگ استاندارد انتخاب بازه تاریخ Material 3
        if (showDatePickerRange) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerRange = false },
                confirmButton = {
                    TextButton(onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis

                        if (startMillis != null && endMillis != null) {
                            val startLocalDate = Instant.ofEpochMilli(startMillis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            val endLocalDate = Instant.ofEpochMilli(endMillis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()

                            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                            startDateText = startLocalDate.format(formatter)
                            endDateText = endLocalDate.format(formatter)

                            // محاسبه اختلاف و دوره قبل
                            daysDifference = ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1
                            val prevStart = startLocalDate.minusDays(daysDifference)
                            val prevEnd = startLocalDate.minusDays(1)
                            previousPeriodText = "از ${prevStart.format(formatter)} تا ${prevEnd.format(formatter)}"
                        }
                        showDatePickerRange = false
                    }) {
                        Text("تایید")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerRange = false }) {
                        Text("انصراف")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f)
                )
            }
        }
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
                targetValue = (incomeData[index] / maxValue),
                animationSpec = tween(durationMillis = 1000), label = ""
            )
            val animatedExpenseHeight by animateFloatAsState(
                targetValue = (expenseData[index] / maxValue),
                animationSpec = tween(durationMillis = 1000), label = ""
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
                            .fillMaxHeight(animatedIncomeHeight)
                            .background(Color(0xFF22C55E), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .fillMaxHeight(animatedExpenseHeight)
                            .background(Color(0xFFEF4444), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(label, fontSize = 10.sp)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        currentValues.forEachIndexed { index, value ->
            val animatedCurrentHeight by animateFloatAsState(
                targetValue = (value / maxValue),
                animationSpec = tween(durationMillis = 1000), label = ""
            )
            val animatedPrevHeight by animateFloatAsState(
                targetValue = (previousValues[index] / maxValue),
                animationSpec = tween(durationMillis = 1000), label = ""
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .fillMaxHeight(animatedPrevHeight)
                        .background(Color.LightGray.copy(alpha = 0.6f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .fillMaxHeight(animatedCurrentHeight)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}

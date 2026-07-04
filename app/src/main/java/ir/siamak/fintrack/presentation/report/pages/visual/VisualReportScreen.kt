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
import com.razaghimahdi.compose_persian_date_picker.PersianDatePickerDialog
import com.razaghimahdi.compose_persian_date_picker.rememberPersianDatePickerState
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import java.time.LocalDate
import java.time.chrono.PersianChronology
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualReportScreen(
    onBackClick: () -> Unit
) {
    // استیت‌های بازه‌های زمانی
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var previousPeriodText by remember { mutableStateOf("بازه قبلی مشخص نیست") }
    var daysDifference by remember { mutableStateOf(0L) }

    // استیت‌های کنترل نمایش دیالوگ‌ها
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberPersianDatePickerState()
    val endDatePickerState = rememberPersianDatePickerState()

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

            // بخش ۱: انتخاب بازه تاریخ شمسی
            item {
                FTCard(onClick = { showStartPicker = true }) {
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
                                "انتخاب بازه زمانی (شمسی)"
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

            // بخش ۲: نمودار اصلی درآمد و هزینه در بازه جاری
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

        // دیالوگ انتخاب تاریخ شروع
        if (showStartPicker) {
            PersianDatePickerDialog(
                onDismissRequest = { showStartPicker = false },
                onDoneClick = { date ->
                    startDateText = "${date.year}/${date.month}/${date.day}"
                    showStartPicker = false
                    showEndPicker = true // باز کردن خودکار دیالوگ تاریخ پایان
                },
                persianDatePickerState = startDatePickerState
            )
        }

        // دیالوگ انتخاب تاریخ پایان
        if (showEndPicker) {
            PersianDatePickerDialog(
                onDismissRequest = { showEndPicker = false },
                onDoneClick = { date ->
                    endDateText = "${date.year}/${date.month}/${date.day}"
                    showEndPicker = false

                    // انجام محاسبات اختلاف روزها و دوره قبلی
                    try {
                        val startJalali = startDatePickerState.getPersianDate()
                        val endJalali = endDatePickerState.getPersianDate()

                        val startLocalDate = LocalDate.ofEpochDay(0)
                            .with(PersianChronology.INSTANCE.date(startJalali.year, startJalali.month, startJalali.day))
                        val endLocalDate = LocalDate.ofEpochDay(0)
                            .with(PersianChronology.INSTANCE.date(endJalali.year, endJalali.month, endJalali.day))

                        daysDifference = ChronoUnit.DAYS.between(startLocalDate, endLocalDate)

                        if (daysDifference >= 0) {
                            val prevStartLocalDate = startLocalDate.minusDays(daysDifference)
                            val prevEndLocalDate = startLocalDate.minusDays(1)

                            val prevStartPersian = PersianChronology.INSTANCE.date(prevStartLocalDate)
                            val prevEndPersian = PersianChronology.INSTANCE.date(prevEndLocalDate)

                            previousPeriodText = "از ${prevStartPersian.get(java.time.temporal.ChronoField.YEAR)}/${prevStartPersian.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)}/${prevStartPersian.get(java.time.temporal.ChronoField.DAY_OF_MONTH)} " +
                                    "تا ${prevEndPersian.get(java.time.temporal.ChronoField.YEAR)}/${prevEndPersian.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)}/${prevEndPersian.get(java.time.temporal.ChronoField.DAY_OF_MONTH)}"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                persianDatePickerState = endDatePickerState
            )
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
                animationSpec = tween(durationMillis = 1000)
            )
            val animatedExpenseHeight by animateFloatAsState(
                targetValue = (expenseData[index] / maxValue),
                animationSpec = tween(durationMillis = 1000)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // ستون درآمد (سبز)
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .fillMaxHeight(animatedIncomeHeight)
                            .background(Color(0xFF22C55E), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                    // ستون هزینه (قرمز)
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
                animationSpec = tween(durationMillis = 1000)
            )
            val animatedPrevHeight by animateFloatAsState(
                targetValue = (previousValues[index] / maxValue),
                animationSpec = tween(durationMillis = 1000)
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // ستون بازه قبلی (خاکستری)
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .fillMaxHeight(animatedPrevHeight)
                        .background(Color.LightGray.copy(alpha = 0.6f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                // ستون بازه فعلی (رنگ اصلی تم)
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

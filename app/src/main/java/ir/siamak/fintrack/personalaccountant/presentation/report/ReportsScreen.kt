package ir.siamak.fintrack.personalaccountant.presentation.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.presentation.report.components.QuickActionsSection
import ir.siamak.fintrack.personalaccountant.presentation.report.components.sectionss.ReportsSummarySection

@Composable
fun ReportsScreen(
    state: ReportsState,
    onMemberReportClick: () -> Unit,
    onWalletReportClick: () -> Unit,
    onHistoryReportClick: () -> Unit,
    onFilteredReportClick: () -> Unit,
    onVisualReportClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // اگر Summary هم می‌خوای باشه
        item {
            ReportsSummarySection(
                income = state.totalIncome,
                expense = state.totalExpense,
                saving = state.totalSaving
            )
        }

        // فقط دسترسی سریع (دیگه هیچ گزارش لیستی اینجا نیست)
        item {
            QuickActionsSection(
                onMemberReportClick = onMemberReportClick,
                onWalletReportClick = onWalletReportClick,
                onHistoryReportClick = onHistoryReportClick,
                onFilteredReportClick = onFilteredReportClick,
                onVisualReportClick = onVisualReportClick
            )
        }
    }
}


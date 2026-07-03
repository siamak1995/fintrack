package ir.siamak.fintrack.presentation.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.report.components.CategoryReportCard
import ir.siamak.fintrack.presentation.report.components.MonthlyReportCard
import ir.siamak.fintrack.presentation.report.components.WalletReportCard
import ir.siamak.fintrack.presentation.report.components.sectionss.ReportsSummarySection

@Composable
fun ReportsScreen(

    state: ReportsState

) {

    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(16.dp),

        verticalArrangement = Arrangement.spacedBy(20.dp)

    ) {

        item {

            ReportsSummarySection(

                income = state.totalIncome,

                expense = state.totalExpense,

                saving = state.totalSaving

            )

        }

        item {

            SectionHeader("گزارش ماهانه")

        }

        items(

            state.monthlyReports

        ) {

            MonthlyReportCard(it)

        }

        item {

            SectionHeader("هزینه براساس دسته")

        }

        items(

            state.categoryReports

        ) {

            CategoryReportCard(it)

        }

        item {

            SectionHeader("کیف پول‌ها")

        }

        items(

            state.walletReports

        ) {

            WalletReportCard(it)

        }

    }

}
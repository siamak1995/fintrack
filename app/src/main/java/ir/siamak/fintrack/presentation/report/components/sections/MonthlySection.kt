package ir.siamak.fintrack.presentation.report.components.sectionss

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.domain.report.MonthlyReportItem
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.report.components.MonthlyReportCard

@Composable
fun MonthlySection(
    reports: List<MonthlyReportItem>
) {

    Column {

        SectionHeader("گزارش ماهانه")

        if (reports.isEmpty()) {

            EmptySectionText("گزارشی وجود ندارد")

            return
        }

        LazyColumn(
            userScrollEnabled = false
        ) {

            items(reports) {

                MonthlyReportCard(it)

            }

        }

    }

}
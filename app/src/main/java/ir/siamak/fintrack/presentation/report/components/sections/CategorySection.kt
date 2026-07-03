package ir.siamak.fintrack.presentation.report.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.domain.report.CategoryReportItem
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.reports.components.CategoryReportCard

@Composable
fun CategorySection(
    reports: List<CategoryReportItem>
) {

    Column {

        SectionHeader("گزارش دسته‌بندی")

        if (reports.isEmpty()) {

            EmptySectionText("داده‌ای وجود ندارد")

            return
        }

        LazyColumn(
            userScrollEnabled = false
        ) {

            items(reports) {

                CategoryReportCard(it)

            }

        }

    }

}
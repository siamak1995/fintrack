package ir.siamak.fintrack.personalaccountant.presentation.report.components.sectionss

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.personalaccountant.domain.report.WalletReportItem
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.personalaccountant.presentation.report.components.WalletReportCard

@Composable
fun WalletSection(
    reports: List<WalletReportItem>
) {

    Column {

        SectionHeader("گزارش حساب‌ها")

        if (reports.isEmpty()) {

            EmptySectionText("داده‌ای وجود ندارد")

            return
        }

        LazyColumn(
            userScrollEnabled = false
        ) {

            items(reports) {

                WalletReportCard(it)

            }

        }

    }

}

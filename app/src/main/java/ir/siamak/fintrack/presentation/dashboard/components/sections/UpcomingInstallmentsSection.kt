package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.items.InstallmentItem

@Composable
fun UpcomingInstallmentsSection(
    installments: List<Installment>
) {

    Column {

        SectionHeader("اقساط")

        if (installments.isEmpty()) {
            EmptySectionText("قسطی وجود ندارد")
            return
        }

        LazyColumn(userScrollEnabled = false) {
            items(installments) { InstallmentItem(it) }
        }
    }
}
package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.items.InstallmentItem

/**
 * بخش نمایش اقساط.
 */
@Composable
fun InstallmentSection(
    installments: List<Installment>
) {
    Column {
        SectionHeader(title = "اقساط")

        if (installments.isEmpty()) {
            EmptySectionText("قسطی ثبت نشده است.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(installments) { installment ->
                    InstallmentItem(installment = installment)
                }
            }
        }
    }
}


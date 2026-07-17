package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.domain.dashboard.items.InstallmentItem
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader


@Composable
fun UpcomingInstallmentsSection(
    installments: List<Installment>,
    onShowAll: () -> Unit = {}
) {
    Column {
        SectionHeader("اقساط پیش رو")
        if (installments.isEmpty()) {
            EmptySectionText("قسطی برای پرداخت وجود ندارد.")
            return
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            installments.take(3).forEach {
                InstallmentItem(installment = it)
            }
        }
        if (installments.size > 3) {
            TextButton(onClick = onShowAll) {
                Text("مشاهده همه")
            }
        }
    }
}
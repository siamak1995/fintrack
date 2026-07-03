package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.domain.dashboard.items.InstallmentItem

@Composable
fun UpcomingInstallmentsSection(
    installments: List<Installment>,
    onShowAll: (() -> Unit)? = null
) {

    Column {

        SectionHeader("اقساط پیش رو")

        if (installments.isEmpty()) {

            EmptySectionText("قسطی ثبت نشده است.")

            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {

            items(
                items = installments.take(3),
                key = { it.id }
            ) {

                InstallmentItem(it)

            }

        }

        if (installments.size > 3 && onShowAll != null) {

            TextButton(
                modifier = Modifier.padding(top = 8.dp),
                onClick = onShowAll
            ) {

                Text("مشاهده همه")

            }

        }

    }

}
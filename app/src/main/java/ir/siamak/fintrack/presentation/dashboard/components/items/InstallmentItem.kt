package ir.siamak.fintrack.presentation.dashboard.components.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText

@Composable
fun InstallmentItem(installment: Installment) {
    FTCard(
        modifier = Modifier
            .padding(top = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = installment.title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            MoneyText(
                amount = installment.totalAmount,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "پرداخت شده: ${"%,.0f".format(installment.paidAmount).replace(" ", "")} تومان",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

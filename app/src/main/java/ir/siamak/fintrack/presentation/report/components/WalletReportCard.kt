package ir.siamak.fintrack.presentation.report.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.domain.report.WalletReportItem
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText

@Composable
fun WalletReportCard(

    item: WalletReportItem

) {

    FTCard {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {

            Text(

                text = item.walletName,

                style = MaterialTheme.typography.titleMedium

            )

            Spacer(
                Modifier.height(8.dp)
            )

            MoneyText(
                amount = item.balance
            )

            Spacer(
                Modifier.height(8.dp)
            )

            LinearProgressIndicator(

                progress = { item.percent / 100f },

                modifier = Modifier.fillMaxWidth()

            )

        }

    }

}
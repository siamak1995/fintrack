package ir.siamak.fintrack.presentation.landing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText

@Composable
fun BalanceCard(
    balance: Double
) {

    FTCard {

        Column(
            Modifier.padding(20.dp)
        ) {

            Text(
                text = "موجودی کل",
                style = MaterialTheme.typography.titleMedium
            )

            MoneyText(
                amount = balance,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

        }

    }

}
package ir.siamak.fintrack.personalaccountant.domain.dashboard.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.presentation.components.FTCard
import ir.siamak.fintrack.personalaccountant.presentation.components.MoneyText
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.parseColorSafely

/**
 * کارت نمایش یک کیف پول.
 *
 * @param wallet داده کیف پول
 * @param onClick رویداد کلیک
 */
@Composable
fun WalletItem(
    wallet: Wallet,
    onClick: () -> Unit
) {
    val walletColor = parseColorSafely(wallet.color)

    FTCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(

                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(walletColor.copy(.15f)),

                    contentAlignment = Alignment.Center

                ) {

                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = walletColor
                    )

                }

                Spacer(Modifier.width(12.dp))

                Column {

                    Text(
                        wallet.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "کیف پول",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                }

            }

            Spacer(Modifier.height(18.dp))

            MoneyText(

                amount = wallet.balance,

                color = walletColor,

                style = MaterialTheme.typography.headlineSmall

            )

        }
    }
}


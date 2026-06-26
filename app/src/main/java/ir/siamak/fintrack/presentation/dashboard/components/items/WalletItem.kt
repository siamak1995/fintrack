package ir.siamak.fintrack.presentation.dashboard.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.dashboard.components.parseColorSafely
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(walletColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = walletColor
                    )
                }

                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MoneyText(
                amount = wallet.balance,
                color = walletColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

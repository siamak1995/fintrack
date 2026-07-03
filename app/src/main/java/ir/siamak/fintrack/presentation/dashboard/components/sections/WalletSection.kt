package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.dashboard.components.items.WalletItem

@Composable
fun WalletSection(

    wallets: List<Wallet>,

    onWalletClick: (Long) -> Unit,

    onAddWalletClick: () -> Unit

) {

    Column {

        Text(

            text = "حساب‌ها",

            style = MaterialTheme.typography.titleLarge,

            modifier = Modifier.padding(bottom = 12.dp)

        )

        if (wallets.isEmpty()) {

            TextButton(

                onClick = onAddWalletClick

            ) {

                Text("اولین حساب را ایجاد کن")

            }

            return

        }

        LazyRow(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            items(wallets) { wallet ->

                WalletItem(

                    wallet = wallet

                ) {

                    onWalletClick(wallet.id)

                }

            }

        }

    }

}
package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.baseinfo.wallet.card.WalletCard

@Composable
fun WalletSection(
    wallets:List<Wallet>,
    onWalletClick:(Long)->Unit,
    onAddWallet:()->Unit
){

    SectionHeader(
        title="کیف پول‌ها",
        icon=Icons.Default.AccountBalanceWallet
    )

    if(wallets.isEmpty()){

        TextButton(
            onClick=onAddWallet
        ){
            androidx.compose.material3.Text("ایجاد اولین کیف پول")
        }

        return
    }

    LazyRow(
        modifier=Modifier
            .fillMaxWidth()
            .padding(top=12.dp),
        horizontalArrangement=Arrangement.spacedBy(16.dp)
    ){

        items(
            items=wallets,
            key={it.id}
        ){

            WalletCard(
                wallet=it,
                onClick={
                    onWalletClick(it.id)
                }
            )

        }

    }

}
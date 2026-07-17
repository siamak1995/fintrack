package ir.siamak.fintrack.presentation.dashboard.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.dashboard.parseColorSafely

@Composable
fun WalletCard(
    wallet:Wallet,
    onClick:()->Unit
){

    val color=parseColorSafely(wallet.color)

    Surface(
        modifier=Modifier
            .width(300.dp)
            .clickable(onClick=onClick),
        shape=RoundedCornerShape(24.dp),
        shadowElevation=6.dp
    ){

        Column(
            modifier=Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            color,
                            color.copy(alpha=.70f)
                        )
                    )
                )
                .padding(20.dp)
        ){

            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.SpaceBetween,
                verticalAlignment=Alignment.CenterVertically
            ){

                Icon(
                    imageVector=Icons.Default.AccountBalanceWallet,
                    contentDescription=null,
                    tint=MaterialTheme.colorScheme.onPrimary
                )

                Icon(
                    imageVector=Icons.Default.CreditCard,
                    contentDescription=null,
                    tint=MaterialTheme.colorScheme.onPrimary
                )

            }

            Spacer(Modifier.height(28.dp))

            Text(
                text=wallet.name,
                color=MaterialTheme.colorScheme.onPrimary,
                style=MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(10.dp))

            MoneyText(
                amount=wallet.balance,
                color=MaterialTheme.colorScheme.onPrimary,
                style=MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(22.dp))

            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.SpaceBetween
            ){

                Column{

                    Text(
                        text="نوع",
                        color=MaterialTheme.colorScheme.onPrimary.copy(.7f),
                        style=MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = wallet.currency.name,
                        color=MaterialTheme.colorScheme.onPrimary
                    )

                }

                Column(
                    horizontalAlignment=Alignment.End
                ){

                    Text(
                        text="واحد پول",
                        color=MaterialTheme.colorScheme.onPrimary.copy(.7f),
                        style=MaterialTheme.typography.labelSmall
                    )



                }

            }

            Spacer(Modifier.height(18.dp))

            Box(
                modifier=Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(.08f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ){

                Text(
                    text="برای مشاهده جزئیات لمس کنید",
                    color=MaterialTheme.colorScheme.onPrimary,
                    style=MaterialTheme.typography.labelMedium
                )

            }

        }

    }

}
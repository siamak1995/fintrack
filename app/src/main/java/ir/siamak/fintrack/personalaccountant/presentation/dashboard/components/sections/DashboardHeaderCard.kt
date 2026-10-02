package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.presentation.components.MoneyText

@Composable
fun DashboardHeaderCard(

    balance: Double,

    income: Double,

    expense: Double,

    saving: Double

) {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        tonalElevation = 4.dp

    ) {

        Column(

            modifier = Modifier
                .background(

                    Brush.linearGradient(

                        listOf(

                            Color(0xff2563EB),

                            Color(0xff4F46E5)

                        )

                    )

                )
                .padding(24.dp)

        ) {

            Text(

                text = "دارایی کل",

                color = Color.White.copy(.8f)

            )

            Spacer(Modifier.height(8.dp))

            MoneyText(

                amount = balance,

                color = Color.White,

                style = MaterialTheme.typography.headlineMedium.copy(

                    fontWeight = FontWeight.Bold

                )

            )

            Spacer(Modifier.height(28.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                MiniInfo(

                    Icons.Default.ArrowUpward,

                    "درآمد",

                    income,

                    Color(0xff4ADE80)

                )

                MiniInfo(

                    Icons.Default.ArrowDownward,

                    "هزینه",

                    expense,

                    Color(0xffF87171)

                )

                MiniInfo(

                    Icons.Default.Savings,

                    "پس‌انداز",

                    saving,

                    Color.White

                )

            }

        }

    }

}

@Composable
private fun MiniInfo(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    title: String,

    amount: Double,

    color: Color

) {

    Column(

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Icon(

            imageVector = icon,

            contentDescription = null,

            tint = color

        )

        Spacer(Modifier.height(6.dp))

        Text(

            text = title,

            color = Color.White.copy(.85f),

            style = MaterialTheme.typography.labelMedium

        )

        MoneyText(

            amount = amount,

            color = color,

            style = MaterialTheme.typography.bodyMedium

        )

    }

}

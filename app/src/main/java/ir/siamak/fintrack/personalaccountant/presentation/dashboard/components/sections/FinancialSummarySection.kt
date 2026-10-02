package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.personalaccountant.presentation.theme.ErrorRed
import ir.siamak.fintrack.personalaccountant.presentation.theme.Success
import java.text.DecimalFormat


@Composable
fun FinancialSummarySection(
    totalBalance: Double,
    walletBalance: Double,
    income: Double,
    expense: Double,
    saving: Double,
    todayIncome: Double,
    todayExpense: Double
) {

    SectionHeader(
        title = "خلاصه مالی",
        icon = Icons.Default.AccountBalanceWallet
    )


    Spacer(modifier = Modifier.height(12.dp))


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SummaryRow(
            firstTitle = "دارایی",
            firstValue = walletBalance,
            firstIcon = Icons.Default.AccountBalanceWallet,
            firstColor = MaterialTheme.colorScheme.primary,

            secondTitle = "پس‌انداز",
            secondValue = saving,
            secondIcon = Icons.Default.Savings,
            secondColor = Color(0xFF10B981)
        )


        SummaryRow(
            firstTitle = "درآمد ماه",
            firstValue = income,
            firstIcon = Icons.Default.TrendingUp,
            firstColor = Success,

            secondTitle = "هزینه ماه",
            secondValue = expense,
            secondIcon = Icons.Default.TrendingDown,
            secondColor = ErrorRed
        )


        SummaryRow(
            firstTitle = "درآمد امروز",
            firstValue = todayIncome,
            firstIcon = Icons.Default.Payments,
            firstColor = Success,

            secondTitle = "هزینه امروز",
            secondValue = todayExpense,
            secondIcon = Icons.Default.Payments,
            secondColor = ErrorRed
        )
    }
}


@Composable
private fun SummaryRow(
    firstTitle: String,
    firstValue: Double,
    firstIcon: androidx.compose.ui.graphics.vector.ImageVector,
    firstColor: Color,

    secondTitle: String,
    secondValue: Double,
    secondIcon: androidx.compose.ui.graphics.vector.ImageVector,
    secondColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        SummaryItem(
            modifier = Modifier.weight(1f),
            title = firstTitle,
            amount = firstValue,
            icon = firstIcon,
            color = firstColor
        )


        Spacer(
            modifier = Modifier
                .height(72.dp)
                .weight(0.02f)
                .background(
                    MaterialTheme.colorScheme.outlineVariant
                )
        )


        SummaryItem(
            modifier = Modifier.weight(1f),
            title = secondTitle,
            amount = secondValue,
            icon = secondIcon,
            color = secondColor
        )
    }
}


@Composable
private fun SummaryItem(
    modifier: Modifier,
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {

    Card(
        modifier = modifier
            .height(92.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(
                    text = formatMoney(amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }


            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(
                        color.copy(alpha = 0.15f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }
        }
    }
}


private fun formatMoney(value: Double): String {

    val formatter = DecimalFormat("#,###")

    return formatter
        .format(value.toLong())
        .replace(',', '٬')
        .map {
            when(it){
                '0' -> '۰'
                '1' -> '۱'
                '2' -> '۲'
                '3' -> '۳'
                '4' -> '۴'
                '5' -> '۵'
                '6' -> '۶'
                '7' -> '۷'
                '8' -> '۸'
                '9' -> '۹'
                else -> it
            }
        }
        .joinToString("")
}

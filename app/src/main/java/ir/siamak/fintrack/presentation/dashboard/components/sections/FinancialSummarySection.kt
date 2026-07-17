package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.SummaryCard
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialSummarySection(
    totalBalance:Double,
    walletBalance:Double,
    income:Double,
    expense:Double,
    saving:Double,
    todayIncome:Double,
    todayExpense:Double
){

    SectionHeader(
        title="خلاصه مالی",
        icon=Icons.Default.AccountBalanceWallet
    )

    FlowRow(
        modifier=Modifier
            .fillMaxWidth()
            .padding(top=12.dp),
        horizontalArrangement=Arrangement.spacedBy(12.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ){

        SummaryCard(
            title="دارایی",
            amount=walletBalance,
            icon=Icons.Default.AccountBalanceWallet,
            iconBackground=MaterialTheme.colorScheme.primary
        )

        SummaryCard(
            title="درآمد ماه",
            amount=income,
            icon=Icons.Default.TrendingUp,
            iconBackground=Success,
            amountColor=Success
        )

        SummaryCard(
            title="هزینه ماه",
            amount=expense,
            icon=Icons.Default.TrendingDown,
            iconBackground=ErrorRed,
            amountColor=ErrorRed
        )

        SummaryCard(
            title="پس‌انداز",
            amount=saving,
            icon=Icons.Default.Savings,
            iconBackground=Color(0xFF10B981),
            amountColor=Color(0xFF10B981)
        )

        SummaryCard(
            title="درآمد امروز",
            amount=todayIncome,
            icon=Icons.Default.Payments,
            iconBackground=Success,
            amountColor=Success
        )

        SummaryCard(
            title="هزینه امروز",
            amount=todayExpense,
            icon=Icons.Default.Payments,
            iconBackground=ErrorRed,
            amountColor=ErrorRed
        )

    }

}
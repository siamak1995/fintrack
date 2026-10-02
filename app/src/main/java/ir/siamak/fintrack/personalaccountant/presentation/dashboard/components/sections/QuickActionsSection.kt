package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.domain.dashboard.items.QuickActionCard
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.SectionHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsSection(
    onWalletClick:()->Unit,
    onTransactionClick:()->Unit,
    onMemberClick:()->Unit,
    onInstallmentClick:()->Unit,
    onReportClick:()->Unit,
    onTransferClick:()->Unit={}
){

    SectionHeader(
        title="دسترسی سریع",
        icon=Icons.Default.SwapHoriz
    )

    FlowRow(
        modifier=Modifier.fillMaxWidth(),
        horizontalArrangement=Arrangement.spacedBy(12.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ){

        QuickActionCard(
            title="کیف پول",
            icon=Icons.Default.AccountBalanceWallet,
            color=Color(0xFF2563EB),
            onClick=onWalletClick
        )

        QuickActionCard(
            title="تراکنش",
            icon=Icons.Default.Payments,
            color=Color(0xFF16A34A),
            onClick=onTransactionClick
        )

        QuickActionCard(
            title="اعضا",
            icon=Icons.Default.Groups,
            color=Color(0xFFF59E0B),
            onClick=onMemberClick
        )

        QuickActionCard(
            title="اقساط",
            icon=Icons.Default.ReceiptLong,
            color=Color(0xFFDB2777),
            onClick=onInstallmentClick
        )

        QuickActionCard(
            title="گزارش",
            icon=Icons.Default.BarChart,
            color=Color(0xFF7C3AED),
            onClick=onReportClick
        )

        QuickActionCard(
            title="انتقال",
            icon=Icons.Default.SwapHoriz,
            color=Color(0xFF0891B2),
            onClick=onTransferClick
        )

    }

}

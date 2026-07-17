package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.dashboard.components.cards.DashboardStatCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardStatisticsSection(

    walletCount: Int,

    memberCount: Int,

    transactionCount: Int,

    installmentCount: Int,

    onWalletClick: () -> Unit = {},

    onMemberClick: () -> Unit = {},

    onTransactionClick: () -> Unit = {},

    onInstallmentClick: () -> Unit = {}

) {

    FlowRow(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(12.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp),

        maxItemsInEachRow = 2

    ) {

        DashboardStatCard(

            modifier = Modifier.weight(1f),

            title = "کیف پول",

            value = walletCount.toString(),

            icon = Icons.Default.AccountBalanceWallet,

            color = Color(0xff2563EB),

            onClick = onWalletClick

        )

        DashboardStatCard(

            modifier = Modifier.weight(1f),

            title = "اعضا",

            value = memberCount.toString(),

            icon = Icons.Default.Groups,

            color = Color(0xffF59E0B),

            onClick = onMemberClick

        )

        DashboardStatCard(

            modifier = Modifier.weight(1f),

            title = "تراکنش",

            value = transactionCount.toString(),

            icon = Icons.Default.Payments,

            color = Color(0xff22C55E),

            onClick = onTransactionClick

        )

        DashboardStatCard(

            modifier = Modifier.weight(1f),

            title = "اقساط",

            value = installmentCount.toString(),

            icon = Icons.Default.ReceiptLong,

            color = Color(0xffEC4899),

            onClick = onInstallmentClick

        )

    }

}
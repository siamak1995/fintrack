package ir.siamak.fintrack.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.dashboard.SectionHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsSection(
    onMemberReportClick: () -> Unit,
    onWalletReportClick: () -> Unit,
    onHistoryReportClick: () -> Unit,
    onFilteredReportClick: () -> Unit,
    onVisualReportClick: () -> Unit
) {
    Column {
        SectionHeader("دسترسی سریع")

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionItem(
                "گزارش اعضا",
                Icons.Default.Groups,
                Color(0xFF3B82F6),
                onMemberReportClick
            )

            ActionItem(
                "گزارش حساب‌ها",
                Icons.Default.AccountBalanceWallet,
                Color(0xFF22C55E),
                onWalletReportClick
            )

            ActionItem(
                "تاریخچه تراکنش",
                Icons.Default.Payments,
                Color(0xFFF59E0B),
                onHistoryReportClick
            )

            // در صورتی که این گزارش‌ها را مجزا طراحی کرده باشی:
            ActionItem(
                "فیلتر پیشرفته",
                Icons.Default.ReceiptLong,
                Color(0xFFEC4899),
                onFilteredReportClick
            )

            ActionItem(
                "گزارش تصویری",
                Icons.Default.BarChart,
                Color(0xFF8B5CF6),
                onVisualReportClick
            )
        }
    }
}

@Composable
private fun ActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    FTCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            color.copy(alpha = .15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

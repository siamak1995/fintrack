package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard

@Composable
fun QuickActionsSection(
    onAddWallet: () -> Unit,
    onAddTransaction: () -> Unit,
    onOpenMembers: () -> Unit,
    onOpenInstallment: () -> Unit,
    onOpenReports: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "حساب",
            onClick = onAddWallet
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            text = "تراکنش",
            onClick = onAddTransaction
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            text = "اعضا",
            onClick = onOpenMembers
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            text = "گزارش",
            onClick = onOpenReports
        )
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {

    FTCard(
        modifier = modifier
            .clickable { onClick() }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}
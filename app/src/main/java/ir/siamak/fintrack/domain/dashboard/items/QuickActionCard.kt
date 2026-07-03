package ir.siamak.fintrack.domain.dashboard.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard

@Composable
fun QuickActionCard(
    text: String,
    icon: ImageVector,
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    icon,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }

        }

    }

}
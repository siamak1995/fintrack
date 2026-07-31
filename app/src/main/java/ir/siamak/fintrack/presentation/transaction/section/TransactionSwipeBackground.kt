package ir.siamak.fintrack.presentation.transaction.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun TransactionSwipeBackground(
    dismissValue: SwipeToDismissBoxValue
) {
    val text = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> "ویرایش"
        SwipeToDismissBoxValue.EndToStart -> "حذف"
        SwipeToDismissBoxValue.Settled -> ""
    }

    val backgroundColor = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
        SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.onPrimaryContainer
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
        SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val alignment = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 22.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = contentColor
        )
    }
}

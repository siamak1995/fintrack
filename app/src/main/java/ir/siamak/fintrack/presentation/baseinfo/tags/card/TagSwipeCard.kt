package ir.siamak.fintrack.presentation.baseinfo.tags.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSwipeCard(
    tag: Tag,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit = {}
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEditClick()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDeleteClick()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            TagSwipeBackground(dismissValue = swipeState.targetValue)
        },
        content = {
            TagCard(
                tag = tag,
                onClick = onClick
            )
        }
    )
}

@Composable
fun TagCard(
    tag: Tag,
    onClick: () -> Unit
) {
    val (typeColor, typeName) = when (tag.allowedType) {
        TransactionType.INCOME -> Color(0xFF2E7D32) to "درآمد"
        TransactionType.EXPENSE -> Color(0xFFC62828) to "هزینه"
        TransactionType.TRANSFER -> Color(0xFF1565C0) to "انتقال"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(typeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "برچسب $typeName",
                    style = MaterialTheme.typography.bodySmall,
                    color = typeColor
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TagSwipeBackground(
    dismissValue: SwipeToDismissBoxValue
) {
    val isEdit = dismissValue == SwipeToDismissBoxValue.StartToEnd

    val backgroundColor = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> PrimaryBlue.copy(alpha = 0.12f)
        SwipeToDismissBoxValue.EndToStart -> ErrorRed.copy(alpha = 0.12f)
        SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    val contentColor = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> PrimaryBlue
        SwipeToDismissBoxValue.EndToStart -> ErrorRed
        SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isEdit) Arrangement.Start else Arrangement.End
    ) {
        if (isEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ویرایش",
                color = contentColor,
                style = MaterialTheme.typography.labelLarge
            )
        } else {
            Text(
                text = "حذف",
                color = contentColor,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}

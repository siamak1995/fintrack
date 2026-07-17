package ir.siamak.fintrack.presentation.baseinfo.member.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSwipeCard(
    member: Member,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
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

                SwipeToDismissBoxValue.Settled -> true
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.fillMaxWidth(0.88f),
        backgroundContent = {
            MemberSwipeBackground(dismissState = dismissState)
        },
        content = {
            MemberCard(member = member)
        }
    )
}

@Composable
private fun MemberCard(member: Member) {
    val accentColor = parseColor(member.color)

    FTCard {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = member.relation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberSwipeBackground(
    dismissState: SwipeToDismissBoxState
) {
    val direction = dismissState.dismissDirection
    val isEdit = direction == SwipeToDismissBoxValue.StartToEnd
    val isDelete = direction == SwipeToDismissBoxValue.EndToStart

    val backgroundColor = when {
        isEdit -> PrimaryBlue.copy(alpha = 0.14f)
        isDelete -> ErrorRed.copy(alpha = 0.14f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    val icon = when {
        isEdit -> Icons.Default.Edit
        isDelete -> Icons.Default.DeleteOutline
        else -> Icons.Default.Person
    }

    val iconTint = when {
        isEdit -> PrimaryBlue
        isDelete -> ErrorRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val alignment = when {
        isDelete -> Alignment.CenterEnd
        else -> Alignment.CenterStart
    }

    val horizontalPadding = when {
        isDelete -> androidx.compose.foundation.layout.PaddingValues(end = 20.dp)
        else -> androidx.compose.foundation.layout.PaddingValues(start = 20.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontalPadding),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

private fun parseColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (_: Exception) {
        PrimaryBlue
    }
}

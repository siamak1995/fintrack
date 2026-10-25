package ir.siamak.fintrack.store.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.formatter.PersianText as Text

/** Same RTL swipe directions as the personal accountant. Financial documents are never deleted here. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreSwipeCard(title: String, details: String, edit: () -> Unit, delete: () -> Unit,
                   image: String? = null, selected: Boolean = false, select: (() -> Unit)? = null) {
    val currentEdit by rememberUpdatedState(edit)
    val currentDelete by rememberUpdatedState(delete)
    var menu by remember { mutableStateOf(false) }
    val swipe = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
        when (value) {
            SwipeToDismissBoxValue.StartToEnd -> currentEdit()
            SwipeToDismissBoxValue.EndToStart -> currentDelete()
            else -> Unit
        }
        false
    })
    SwipeToDismissBox(state = swipe, modifier = Modifier.fillMaxWidth(), backgroundContent = {
        val editing = swipe.dismissDirection == SwipeToDismissBoxValue.StartToEnd
        Box(Modifier.fillMaxSize().clip(MaterialTheme.shapes.large)
            .background(if(editing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
            .padding(24.dp), contentAlignment = if(editing) Alignment.CenterStart else Alignment.CenterEnd) {
            Icon(if(editing) Icons.Outlined.Edit else Icons.Outlined.DeleteOutline, if(editing) "ویرایش" else "حذف")
        }
    }) {
        OutlinedCard(Modifier.fillMaxWidth().semantics {
            customActions = listOf(CustomAccessibilityAction("ویرایش", { edit(); true }), CustomAccessibilityAction("حذف", { delete(); true }))
        }, colors = CardDefaults.outlinedCardColors(containerColor = if(selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if(image != null) Picture(image, Modifier.size(56.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(details, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    select?.let { action -> TextButton(onClick = action, enabled = !selected) { Text(if(selected) "فروشگاه فعال" else "فعال‌کردن این فروشگاه") } }
                }
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Outlined.MoreVert, "عملیات " + title) }
                    DropdownMenu(menu, { menu = false }) {
                        DropdownMenuItem(text = { Text("ویرایش") }, onClick = { menu = false; edit() })
                        DropdownMenuItem(text = { Text("حذف") }, onClick = { menu = false; delete() })
                    }
                }
            }
        }
    }
}

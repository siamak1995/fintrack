package ir.siamak.fintrack.presentation.baseinfo.tags.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTTopBar

@Composable
fun TagListScreen(
    viewModel: TagListViewModel,
    onBack: () -> Unit,
    onAddTag: () -> Unit,
    onEditTag: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            FTTopBar(
                title = "تگ‌ها",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                actionIcon = Icons.Default.Add,
                actionContentDescription = "افزودن تگ",
                onActionClick = onAddTag
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(state.tags) { tag ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = tag.name,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { onEditTag(tag.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "ویرایش تگ"
                            )
                        }

                        IconButton(
                            onClick = { viewModel.onEvent(TagListEvent.Delete(tag.id)) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف تگ"
                            )
                        }
                    }
                }
            }
        }
    }
}

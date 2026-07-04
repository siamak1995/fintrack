package ir.siamak.fintrack.presentation.baseinfo.tags.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.domain.model.Tag
import ir.siamak.fintrack.presentation.components.FTTopBar

@Composable
fun TagListScreen(
    viewModel: TagListViewModel,
    onBack: () -> Unit,
    onAddTag: () -> Unit,
    onEditTag: (Int) -> Unit
) {
    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            FTTopBar(
                title = "تگ‌ها",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onAddTag) {
                        Icon(Icons.Default.Add, contentDescription = "add-tag")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(state.value.tags.size) { index ->
                val tag = state.value.tags[index]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
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
                            Icon(Icons.Default.Edit, contentDescription = "edit")
                        }

                        IconButton(onClick = { viewModel.onEvent(TagListEvent.Delete(tag)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "delete")
                        }
                    }
                }
            }
        }
    }
}

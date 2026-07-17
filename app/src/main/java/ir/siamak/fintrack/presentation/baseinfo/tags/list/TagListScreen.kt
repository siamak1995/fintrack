package ir.siamak.fintrack.presentation.baseinfo.tags.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.baseinfo.tags.card.TagSwipeCard
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = state.tags,
                key = { tag -> tag.id }
            ) { tag ->
                TagSwipeCard(
                    tagId = tag.id,
                    tagName = tag.name,
                    onEditClick = { onEditTag(tag.id) },
                    onDeleteClick = { viewModel.onEvent(TagListEvent.Delete(tag.id)) }
                )
            }
        }
    }
}

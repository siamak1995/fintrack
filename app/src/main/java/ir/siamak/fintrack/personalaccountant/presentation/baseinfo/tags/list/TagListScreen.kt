package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.list

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.card.TagSwipeCard
import ir.siamak.fintrack.personalaccountant.presentation.components.FTTopBar

@Composable
fun TagListScreen(
    onBack: () -> Unit,
    onAddTag: () -> Unit,
    onEditTag: (Long) -> Unit,
    viewModel: TagListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    ) { paddingValues ->
        TagListContent(
            paddingValues = paddingValues,
            state = state,
            onEditTag = onEditTag,
            onDeleteTag = { tagId ->
                viewModel.onEvent(TagListEvent.Delete(tagId))
            }
        )
    }
}

@Composable
private fun TagListContent(
    paddingValues: PaddingValues,
    state: TagListState,
    onEditTag: (Long) -> Unit,
    onDeleteTag: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = state.tags,
            key = { tag -> tag.id }
        ) { tag ->
            TagSwipeCard(
                tag = tag,
                onEditClick = { onEditTag(tag.id) },
                onDeleteClick = { onDeleteTag(tag.id) },
                onClick = { onEditTag(tag.id) }
            )
        }
    }
}


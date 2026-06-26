package ir.siamak.fintrack.presentation.member.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MemberRoute(
    onAddMemberClick: () -> Unit,
    onEditMemberClick: (Long) -> Unit,
    viewModel: MemberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MemberScreen(
        uiState = uiState,
        onAddMemberClick = onAddMemberClick,
        onEditMemberClick = onEditMemberClick,
        onDeleteMemberClick = viewModel::deleteMember
    )
}

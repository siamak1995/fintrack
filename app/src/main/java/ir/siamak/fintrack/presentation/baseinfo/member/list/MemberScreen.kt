package ir.siamak.fintrack.presentation.baseinfo.member.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.presentation.baseinfo.member.card.MemberSwipeCard
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.FTTopBar
import ir.siamak.fintrack.presentation.theme.PrimaryBlue

@Composable
fun MemberScreen(
    uiState: MemberUiState,
    onAddMemberClick: () -> Unit,
    onEditMemberClick: (Long) -> Unit,
    onDeleteMemberClick: (Member) -> Unit
) {
    Scaffold(
        topBar = {
            FTTopBar(
                title = "اعضای خانواده",
                subtitle = "مدیریت اعضای ثبت‌شده",
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMemberClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("عضو جدید") }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                MemberMessageState(
                    modifier = Modifier.padding(padding),
                    title = "خطا در دریافت اطلاعات",
                    message = uiState.error
                )
            }

            uiState.members.isEmpty() -> {
                MemberMessageState(
                    modifier = Modifier.padding(padding),
                    title = "هنوز عضوی ثبت نشده",
                    message = "برای شروع، یک عضو جدید اضافه کن."
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = uiState.members,
                        key = { member -> member.id }
                    ) { member ->
                        MemberSwipeCard(
                            member = member,
                            onEditClick = { onEditMemberClick(member.id) },
                            onDeleteClick = { onDeleteMemberClick(member) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberMessageState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FTCard(
            modifier = Modifier.padding(24.dp)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

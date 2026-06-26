package ir.siamak.fintrack.presentation.member.add_edit_member

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.presentation.components.FTTopBar

@Composable
fun AddEditMemberScreen(
    memberId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditMemberViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val memberColors = listOf(
        "#2196F3",
        "#4CAF50",
        "#FF9800",
        "#9C27B0",
        "#F44336",
        "#009688"
    )

    val memberIcons = listOf(
        "ic_default_user",
        "ic_child",
        "ic_spouse",
        "ic_parent"
    )

    LaunchedEffect(memberId) {
        memberId?.let {
            viewModel.onEvent(AddEditMemberEvent.LoadMember(it))
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                FTTopBar(
                    title = if (memberId == null) "عضو جدید" else "ویرایش عضو",
                    subtitle = "اطلاعات عضو خانواده را وارد کنید",
                    actionIcon = Icons.Default.ArrowForward,
                    onActionClick = onBack,
                    modifier = Modifier.padding(16.dp)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = {
                        viewModel.onEvent(AddEditMemberEvent.EnteredName(it))
                    },
                    label = { Text("نام عضو") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.relation,
                    onValueChange = {
                        viewModel.onEvent(AddEditMemberEvent.EnteredRelation(it))
                    },
                    label = { Text("نسبت (مثلاً همسر، فرزند)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "رنگ عضو",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    memberColors.forEach { colorHex ->
                        val isSelected = state.color == colorHex

                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 44.dp else 36.dp)
                                .background(
                                    color = Color(android.graphics.Color.parseColor(colorHex)),
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.onEvent(AddEditMemberEvent.EnteredColor(colorHex))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Text(
                                    text = "✓",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "آیکون عضو",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    memberIcons.forEach { iconName ->
                        val isSelected = state.icon == iconName

                        Surface(
                            modifier = Modifier
                                .size(56.dp)
                                .clickable {
                                    viewModel.onEvent(AddEditMemberEvent.EnteredIcon(iconName))
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (iconName) {
                                        "ic_child" -> Icons.Default.ChildCare
                                        "ic_spouse" -> Icons.Default.Face
                                        "ic_parent" -> Icons.Default.SupervisorAccount
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = iconName,
                                    tint = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.onEvent(AddEditMemberEvent.SaveMember) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = !state.isLoading &&
                            state.name.isNotBlank() &&
                            state.relation.isNotBlank()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ذخیره عضو", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

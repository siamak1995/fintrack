package ir.siamak.fintrack.presentation.transaction.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.components.FTTopBar
import ir.siamak.fintrack.presentation.transaction.TransactionList
import ir.siamak.fintrack.presentation.transaction.TransactionListEmptyState
import ir.siamak.fintrack.presentation.transaction.TransactionListErrorState
import ir.siamak.fintrack.presentation.transaction.TransactionListEvent
import ir.siamak.fintrack.presentation.transaction.TransactionListState
import ir.siamak.fintrack.presentation.transaction.TransactionListViewModel

@Composable
fun TransactionListRoute(
    onBack: () -> Unit,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Transaction?>(null) }
    val errorMessage = state.errorMessage

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("حذف تراکنش") },
            text = { Text("آیا از حذف این تراکنش مطمئن هستید؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete?.let(viewModel::deleteTransaction)
                        pendingDelete = null
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDelete = null }
                ) {
                    Text("انصراف")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FTTopBar(
                title = "تراکنش‌ها",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTransaction,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن تراکنش"
                    )
                },
                text = { Text("تراکنش جدید") }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null && state.allTransactions.isEmpty() -> {
                TransactionListErrorState(
                    message = errorMessage,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                TransactionListContent(
                    state = state,
                    innerPadding = innerPadding,
                    onEvent = viewModel::onEvent,
                    onEditTransaction = onEditTransaction,
                    onDeleteTransaction = { pendingDelete = it }
                )
            }
        }
    }
}

@Composable
private fun TransactionListContent(
    state: TransactionListState,
    innerPadding: PaddingValues,
    onEvent: (TransactionListEvent) -> Unit,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        FilterSection(
            selectedMemberId = state.filter.selectedMemberId,
            selectedWalletId = state.filter.selectedWalletId,
            selectedTagIds = state.filter.selectedTagIds,
            members = state.members,
            wallets = state.wallets,
            tags = state.tags,
            onMemberSelected = {
                onEvent(TransactionListEvent.OnMemberFilterSelected(it))
            },
            onWalletSelected = {
                onEvent(TransactionListEvent.OnWalletFilterSelected(it))
            },
            onTagToggled = { tagId ->
                onEvent(TransactionListEvent.OnTagFilterToggled(tagId))
            },
            onAllTagsSelected = {
                onEvent(TransactionListEvent.OnAllTagsSelected)
            },
            onClearFilters = {
                onEvent(TransactionListEvent.OnClearFiltersClicked)
            },
            hasActiveFilters = state.filter.hasActiveFilters
        )

        FilterSummary(
            filteredCount = state.filteredCount,
            hasActiveFilters = state.filter.hasActiveFilters
        )

        when {
            state.isCompletelyEmpty -> {
                TransactionListEmptyState(
                    title = "هنوز تراکنشی ثبت نشده است",
                    description = "برای شروع، اولین تراکنش خود را ثبت کنید."
                )
            }

            state.isFilteredEmpty -> {
                TransactionListEmptyState(
                    title = "تراکنشی با فیلترهای انتخاب‌شده پیدا نشد",
                    description = "فیلترها را تغییر دهید یا پاک کنید."
                )
            }

            else -> {
                TransactionList(
                    transactions = state.filteredTransactions,
                    onEditTransaction = onEditTransaction,
                    onDeleteTransaction = onDeleteTransaction
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    selectedMemberId: Long?,
    selectedWalletId: Long?,
    selectedTagIds: Set<Long>,
    members: List<Member>,
    wallets: List<Wallet>,
    tags: List<Tag>,
    onMemberSelected: (Long?) -> Unit,
    onWalletSelected: (Long?) -> Unit,
    onTagToggled: (Long) -> Unit,
    onAllTagsSelected: () -> Unit,
    onClearFilters: () -> Unit,
    hasActiveFilters: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "فیلترها",
            style = MaterialTheme.typography.titleMedium
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "اعضا",
                style = MaterialTheme.typography.labelLarge
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedMemberId == null,
                    onClick = { onMemberSelected(null) },
                    label = { Text("همه") }
                )

                members.forEach { member ->
                    FilterChip(
                        selected = selectedMemberId == member.id,
                        onClick = { onMemberSelected(member.id) },
                        label = { Text(member.name) }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "حساب‌ها",
                style = MaterialTheme.typography.labelLarge
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedWalletId == null,
                    onClick = { onWalletSelected(null) },
                    label = { Text("همه") }
                )

                wallets.forEach { wallet ->
                    FilterChip(
                        selected = selectedWalletId == wallet.id,
                        onClick = { onWalletSelected(wallet.id) },
                        label = { Text(wallet.name) }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "تگ‌ها",
                style = MaterialTheme.typography.labelLarge
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTagIds.isEmpty(),
                    onClick = onAllTagsSelected,
                    label = { Text("همه") }
                )

                tags.forEach { tag ->
                    FilterChip(
                        selected = selectedTagIds.contains(tag.id),
                        onClick = { onTagToggled(tag.id) },
                        label = { Text(tag.name) }
                    )
                }
            }
        }

        if (hasActiveFilters) {
            AssistChip(
                onClick = onClearFilters,
                label = { Text("پاک کردن فیلترها") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterAltOff,
                        contentDescription = "پاک کردن فیلترها",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }

        HorizontalDivider()
    }
}

@Composable
private fun FilterSummary(
    filteredCount: Int,
    hasActiveFilters: Boolean
) {
    if (!hasActiveFilters) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$filteredCount نتیجه پیدا شد",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

package ir.siamak.fintrack.presentation.transaction.navigation

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import ir.siamak.fintrack.presentation.transaction.TransactionListEvent
import ir.siamak.fintrack.presentation.transaction.TransactionListFilter
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
                TransactionListEmptyState(
                    modifier = Modifier.padding(innerPadding),
                    title = "خطا در بارگذاری تراکنش‌ها",
                    description = errorMessage
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
    var isFilterExpanded by rememberSaveable { mutableStateOf(false) }
    var draftFilter by remember(state.filter) { mutableStateOf(state.filter) }
    val membersById = remember(state.members) { state.members.associateBy { it.id } }
    val walletsById = remember(state.wallets) { state.wallets.associateBy { it.id } }


    LaunchedEffect(isFilterExpanded, state.filter) {
        if (isFilterExpanded) {
            draftFilter = state.filter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        FilterSection(
            filter = draftFilter,
            members = state.members,
            wallets = state.wallets,
            tags = state.tags,
            isExpanded = isFilterExpanded,
            onExpandToggle = {
                if (!isFilterExpanded) {
                    draftFilter = state.filter
                }
                isFilterExpanded = !isFilterExpanded
            },
            onMemberSelected = { memberId ->
                draftFilter = draftFilter.copy(selectedMemberId = memberId)
            },
            onWalletSelected = { walletId ->
                draftFilter = draftFilter.copy(selectedWalletId = walletId)
            },
            onTagToggled = { tagId ->
                val updatedTags = draftFilter.selectedTagIds.toMutableSet().apply {
                    if (contains(tagId)) remove(tagId) else add(tagId)
                }
                draftFilter = draftFilter.copy(selectedTagIds = updatedTags)
            },
            onClearClicked = {
                draftFilter = TransactionListFilter()
                onEvent(TransactionListEvent.OnClearFiltersClicked)
            },
            onApplyClicked = {
                onEvent(TransactionListEvent.OnApplyFilterClicked(draftFilter))
                isFilterExpanded = false
            }
        )

        FilterSummary(
            filteredCount = state.filteredCount,
            hasActiveFilters = state.filter.hasActiveFilters
        )

        HorizontalDivider()

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
                    membersById = membersById,
                    walletsById = walletsById,
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
    filter: TransactionListFilter,
    members: List<Member>,
    wallets: List<Wallet>,
    tags: List<Tag>,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onMemberSelected: (Long?) -> Unit,
    onWalletSelected: (Long?) -> Unit,
    onTagToggled: (Long) -> Unit,
    onClearClicked: () -> Unit,
    onApplyClicked: () -> Unit
) {
    val activeFilterCount = buildList {
        if (filter.selectedMemberId != null) add(Unit)
        if (filter.selectedWalletId != null) add(Unit)
        if (filter.selectedTagIds.isNotEmpty()) add(Unit)
    }.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onExpandToggle,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (activeFilterCount > 0) {
                    "انتخاب فیلتر ($activeFilterCount)"
                } else {
                    "انتخاب فیلتر"
                },
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "فیلتر تراکنش‌ها",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FilterDropdownField(
                        title = "عضو",
                        selectedText = members.firstOrNull {
                            it.id == filter.selectedMemberId
                        }?.name ?: "همه",
                        options = listOf(null to "همه") + members.map { it.id to it.name },
                        onSelected = onMemberSelected
                    )

                    FilterDropdownField(
                        title = "کیف پول",
                        selectedText = wallets.firstOrNull {
                            it.id == filter.selectedWalletId
                        }?.name ?: "همه",
                        options = listOf(null to "همه") + wallets.map { it.id to it.name },
                        onSelected = onWalletSelected
                    )

                    Text(
                        text = "تگ‌ها",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filter.selectedTagIds.isEmpty(),
                            onClick = {
                                filter.selectedTagIds.forEach { tagId ->
                                    onTagToggled(tagId)
                                }
                            },
                            label = { Text("همه") }
                        )

                        tags.forEach { tag ->
                            FilterChip(
                                selected = tag.id in filter.selectedTagIds,
                                onClick = { onTagToggled(tag.id) },
                                label = { Text(tag.name) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClearClicked,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAltOff,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(" پاک کردن")
                        }

                        Button(
                            onClick = onApplyClicked,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("اعمال فیلتر")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdownField(
    title: String,
    selectedText: String,
    options: List<Pair<Long?, String>>,
    onSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (id, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSelected(id)
                            expanded = false
                        }
                    )
                }
            }
        }
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
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$filteredCount نتیجه پیدا شد",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

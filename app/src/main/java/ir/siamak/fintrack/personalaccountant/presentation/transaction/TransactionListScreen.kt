package ir.siamak.fintrack.personalaccountant.presentation.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.FilterAltOff
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.presentation.components.EmptyState
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.FilterSection
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.TransactionTypeFilterRow

@Composable
fun TransactionListScreen(
    state: TransactionListState,
    onEvent: (TransactionListEvent) -> Unit,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    var isFilterExpanded by rememberSaveable { mutableStateOf(false) }

    var draftFilter by remember(state.filter) {
        mutableStateOf(state.filter)
    }

    var draftType by remember(state.selectedTransactionType) {
        mutableStateOf(state.selectedTransactionType)
    }

    val membersById = remember(state.members) {
        state.members.associateBy(Member::id)
    }

    val walletsById = remember(state.wallets) {
        state.wallets.associateBy(Wallet::id)
    }

    val activeFilterCount = remember(
        state.filter.selectedMemberId,
        state.filter.selectedWalletId,
        state.filter.selectedTagIds,
        state.selectedTransactionType
    ) {
        var count = 0
        if (state.filter.selectedMemberId != null) count++
        if (state.filter.selectedWalletId != null) count++
        if (state.selectedTransactionType != null) count++
        count += state.filter.selectedTagIds.size
        count
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilterSummaryBar(
                activeFilterCount = activeFilterCount,
                isExpanded = isFilterExpanded,
                onToggleExpanded = {
                    isFilterExpanded = !isFilterExpanded
                },
                onClearFilters = {
                    draftFilter = TransactionListFilter()
                    draftType = null
                    onEvent(TransactionListEvent.OnClearFiltersClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            AnimatedVisibility(
                visible = isFilterExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 520.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TransactionTypeFilterRow(
                            selectedType = draftType,
                            onTypeSelected = { type ->
                                draftType = type
                                onEvent(TransactionListEvent.OnTypeFilterChanged(type))
                            }
                        )

                        HorizontalDivider()

                        FilterSection(
                            filter = draftFilter,
                            members = state.members,
                            wallets = state.wallets,
                            tags = state.availableTags,
                            isExpanded = true,
                            onExpandToggle = { },
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
                                draftType = null
                                onEvent(TransactionListEvent.OnClearFiltersClicked)
                            },
                            onApplyClicked = {
                                onEvent(TransactionListEvent.OnApplyFilterClicked(draftFilter))
                                isFilterExpanded = false
                            }
                        )


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    draftFilter = TransactionListFilter()
                                    draftType = null
                                    onEvent(TransactionListEvent.OnClearFiltersClicked)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("حذف فیلتر")
                            }

                            Button(
                                onClick = {
                                    onEvent(TransactionListEvent.OnApplyFilterClicked(draftFilter))
                                    isFilterExpanded = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("اعمال فیلتر")
                            }
                        }
                    }
                }
            }

            when {
                state.isLoading -> {
                    LoadingPlaceholder(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                }

                state.isCompletelyEmpty -> {
                    EmptyState(
                        title = "هنوز تراکنشی ثبت نشده",
                        description = "برای شروع مدیریت مالی، اولین تراکنش خود را ثبت کنید.",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                state.isFilteredEmpty -> {
                    EmptyState(
                        title = "تراکنشی پیدا نشد",
                        description = "هیچ تراکنشی با فیلترهای انتخاب‌شده مطابقت ندارد.",
                        modifier = Modifier.fillMaxSize()
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
}

@Composable
private fun FilterSummaryBar(
    activeFilterCount: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onToggleExpanded,
                    label = {
                        Text(
                            if (activeFilterCount > 0) {
                                "فیلترها ($activeFilterCount)"
                            } else {
                                "فیلترها"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (activeFilterCount > 0) {
                                Icons.Rounded.FilterAlt
                            } else {
                                Icons.Rounded.FilterAltOff
                            },
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (isExpanded) {
                                Icons.Rounded.KeyboardArrowUp
                            } else {
                                Icons.Rounded.KeyboardArrowDown
                            },
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                    )
                )

                if (activeFilterCount > 0) {
                    OutlinedButton(
                        onClick = onClearFilters
                    ) {
                        Text("پاک کردن")
                    }
                }
            }

            if (activeFilterCount > 0) {
                Text(
                    text = "فیلتر فعال: $activeFilterCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "برای محدود کردن لیست، فیلترها را باز کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


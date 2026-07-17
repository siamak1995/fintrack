package ir.siamak.fintrack.presentation.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Wallet

@Composable
fun TransactionListScreen(
    state: TransactionListState,
    onEvent: (TransactionListEvent) -> Unit,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (ir.siamak.fintrack.data.model.Transaction) -> Unit
) {
    var isFilterExpanded by rememberSaveable { mutableStateOf(false) }
    var draftFilter by remember(state.filter) { mutableStateOf(state.filter) }

    LaunchedEffect(isFilterExpanded, state.filter) {
        if (isFilterExpanded) {
            draftFilter = state.filter
        }
    }

    val membersById = remember(state.members) { state.members.associateBy { it.id } }
    val walletsById = remember(state.wallets) { state.wallets.associateBy { it.id } }

    Column(
        modifier = Modifier.fillMaxSize()
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
            },
            onApplyClicked = {
                onEvent(TransactionListEvent.OnApplyFilterClicked(draftFilter))
                isFilterExpanded = false
            }
        )

        TransactionList(
            transactions = state.filteredTransactions,
            membersById = membersById,
            walletsById = walletsById,
            onEditTransaction = onEditTransaction,
            onDeleteTransaction = onDeleteTransaction
        )
    }
}

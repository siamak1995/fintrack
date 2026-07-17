package ir.siamak.fintrack.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.repository.TagRepository
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for transaction list screen.
 *
 * This ViewModel:
 * - loads transactions, members, wallets and tags
 * - stores current filter state
 * - applies in-memory filtering for MVP
 */
@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases,
    private val memberUseCases: MemberUseCases,
    private val walletUseCases: WalletUseCases,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState(isLoading = true))
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    init {
        observeScreenData()
    }

    /**
     * Handles UI events.
     */
    fun onEvent(event: TransactionListEvent) {
        when (event) {
            is TransactionListEvent.OnMemberFilterSelected -> {
                _state.update { current ->
                    val updatedFilter = current.filter.copy(selectedMemberId = event.memberId)
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(current.allTransactions, updatedFilter)
                    )
                }
            }

            is TransactionListEvent.OnWalletFilterSelected -> {
                _state.update { current ->
                    val updatedFilter = current.filter.copy(selectedWalletId = event.walletId)
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(current.allTransactions, updatedFilter)
                    )
                }
            }

            is TransactionListEvent.OnTagFilterToggled -> {
                _state.update { current ->
                    val updatedTagIds = current.filter.selectedTagIds.toMutableSet().apply {
                        if (contains(event.tagId)) remove(event.tagId) else add(event.tagId)
                    }

                    val updatedFilter = current.filter.copy(selectedTagIds = updatedTagIds)

                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(current.allTransactions, updatedFilter)
                    )
                }
            }

            TransactionListEvent.OnAllTagsSelected -> {
                _state.update { current ->
                    val updatedFilter = current.filter.copy(selectedTagIds = emptySet())
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(current.allTransactions, updatedFilter)
                    )
                }
            }

            TransactionListEvent.OnClearFiltersClicked -> {
                _state.update { current ->
                    val updatedFilter = TransactionListFilter()
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(current.allTransactions, updatedFilter)
                    )
                }
            }
        }
    }

    /**
     * Observes all screen data streams and updates state.
     */
    private fun observeScreenData() {
        viewModelScope.launch {
            combine(
                transactionUseCases.getAllTransactions(),
                memberUseCases.getAllMembers(),
                walletUseCases.getAllWallets(),
                tagRepository.getAllTags()
            ) { transactions, members, wallets, tags ->
                ScreenData(
                    transactions = transactions,
                    members = members,
                    wallets = wallets,
                    tags = tags
                )
            }.collect { data ->
                _state.update { current ->
                    val filtered = applyFilters(data.transactions, current.filter)

                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        allTransactions = data.transactions,
                        filteredTransactions = filtered,
                        members = data.members,
                        wallets = data.wallets,
                        tags = data.tags
                    )
                }
            }
        }
    }

    /**
     * Applies current filters to the source transaction list.
     */
    private fun applyFilters(
        transactions: List<Transaction>,
        filter: TransactionListFilter
    ): List<Transaction> {
        return transactions.filter { transaction ->
            val memberMatches = filter.selectedMemberId == null ||
                    transaction.memberId == filter.selectedMemberId

            val walletMatches = filter.selectedWalletId == null ||
                    transaction.walletId == filter.selectedWalletId

            val tagMatches = filter.selectedTagIds.isEmpty() ||
                    transaction.tags.any { tag -> tag.id in filter.selectedTagIds }

            memberMatches && walletMatches && tagMatches
        }
    }

    /**
     * Aggregated data required by the screen.
     */
    private data class ScreenData(
        val transactions: List<Transaction>,
        val members: List<Member>,
        val wallets: List<Wallet>,
        val tags: List<Tag>
    )
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionUseCases.deleteTransaction(transaction)
        }
    }

}

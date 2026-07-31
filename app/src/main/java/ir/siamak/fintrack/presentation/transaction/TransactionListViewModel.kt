package ir.siamak.fintrack.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
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
 * ViewModel responsible for managing transaction list screen state,
 * filters, related base data, and available tags based on transaction type.
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

    fun onEvent(event: TransactionListEvent) {
        when (event) {
            is TransactionListEvent.OnApplyFilterClicked -> {
                _state.update { current ->
                    current.copy(
                        filter = event.filter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = event.filter
                        )
                    )
                }
            }

            TransactionListEvent.OnClearFiltersClicked -> {
                val newFilter = TransactionListFilter()
                _state.update { current ->
                    current.copy(
                        filter = newFilter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = newFilter
                        )
                    )
                }
            }

            is TransactionListEvent.OnMemberFilterSelected -> {
                _state.update { current ->
                    val updatedFilter = current.filter.copy(
                        selectedMemberId = event.memberId
                    )
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = updatedFilter
                        )
                    )
                }
            }

            is TransactionListEvent.OnWalletFilterSelected -> {
                _state.update { current ->
                    val updatedFilter = current.filter.copy(
                        selectedWalletId = event.walletId
                    )
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = updatedFilter
                        )
                    )
                }
            }

            is TransactionListEvent.OnTagFilterToggled -> {
                _state.update { current ->
                    val currentTagIds = current.filter.selectedTagIds
                    val updatedTagIds = if (event.tagId in currentTagIds) {
                        currentTagIds - event.tagId
                    } else {
                        currentTagIds + event.tagId
                    }

                    val updatedFilter = current.filter.copy(
                        selectedTagIds = updatedTagIds
                    )

                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = updatedFilter
                        )
                    )
                }
            }

            TransactionListEvent.OnAllTagsSelected -> {
                _state.update { current ->
                    val updatedFilter = current.filter.copy(
                        selectedTagIds = emptySet()
                    )
                    current.copy(
                        filter = updatedFilter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = updatedFilter
                        )
                    )
                }
            }
        }
    }

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
                    val selectedType = current.selectedTransactionType
                    val availableTags = if (selectedType == null) {
                        data.tags
                    } else {
                        data.tags.filter { it.allowedType == selectedType }
                    }

                    val filteredTransactions = applyFilters(
                        transactions = data.transactions,
                        filter = current.filter
                    )

                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        allTransactions = data.transactions,
                        filteredTransactions = filteredTransactions,
                        members = data.members,
                        wallets = data.wallets,
                        tags = data.tags,
                        availableTags = availableTags
                    )
                }
            }
        }
    }

    private fun applyFilters(
        transactions: List<Transaction>,
        filter: TransactionListFilter
    ): List<Transaction> {
        return transactions.filter { transaction ->
            (filter.selectedMemberId == null || transaction.memberId == filter.selectedMemberId) &&
                    (filter.selectedWalletId == null || transaction.walletId == filter.selectedWalletId) &&
                    (filter.selectedTagIds.isEmpty() || transaction.tags.any { tag -> tag.id in filter.selectedTagIds })
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionUseCases.deleteTransaction(transaction)
        }
    }

    fun onTransactionTypeChanged(newType: TransactionType?) {
        _state.update { current ->
            val filteredTags = if (newType == null) {
                current.tags
            } else {
                current.tags.filter { it.allowedType == newType }
            }

            current.copy(
                selectedTransactionType = newType,
                availableTags = filteredTags
            )
        }
    }

    private data class ScreenData(
        val transactions: List<Transaction>,
        val members: List<Member>,
        val wallets: List<Wallet>,
        val tags: List<Tag>
    )
}

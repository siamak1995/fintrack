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
                val clearedFilter = TransactionListFilter()

                _state.update { current ->
                    current.copy(
                        filter = clearedFilter,
                        filteredTransactions = applyFilters(
                            transactions = current.allTransactions,
                            filter = clearedFilter
                        )
                    )
                }
            }

            is TransactionListEvent.OnMemberFilterSelected -> Unit
            is TransactionListEvent.OnWalletFilterSelected -> Unit
            is TransactionListEvent.OnTagFilterToggled -> Unit
            TransactionListEvent.OnAllTagsSelected -> Unit
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
                        tags = data.tags
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
            val memberMatches = filter.selectedMemberId == null ||
                    transaction.memberId == filter.selectedMemberId

            val walletMatches = filter.selectedWalletId == null ||
                    transaction.walletId == filter.selectedWalletId

            val tagMatches = filter.selectedTagIds.isEmpty() ||
                    transaction.tags.any { tag -> tag.id in filter.selectedTagIds }

            memberMatches && walletMatches && tagMatches
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionUseCases.deleteTransaction(transaction)
        }
    }

    private data class ScreenData(
        val transactions: List<Transaction>,
        val members: List<Member>,
        val wallets: List<Wallet>,
        val tags: List<Tag>
    )
}

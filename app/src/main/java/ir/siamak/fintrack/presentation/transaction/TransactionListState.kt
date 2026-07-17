package ir.siamak.fintrack.presentation.transaction

import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet

/**
 * UI state for transaction list screen.
 */
data class TransactionListState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val allTransactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val members: List<Member> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val filter: TransactionListFilter = TransactionListFilter()
) {
    /**
     * Returns true when there is no transaction at all.
     */
    val isCompletelyEmpty: Boolean
        get() = allTransactions.isEmpty()

    /**
     * Returns true when source data exists but current filters return no results.
     */
    val isFilteredEmpty: Boolean
        get() = allTransactions.isNotEmpty() &&
                filteredTransactions.isEmpty() &&
                filter.hasActiveFilters

    /**
     * Returns filtered result count.
     */
    val filteredCount: Int
        get() = filteredTransactions.size
}

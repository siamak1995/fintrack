package ir.siamak.fintrack.personalaccountant.presentation.transaction

import ir.siamak.fintrack.personalaccountant.data.model.TransactionType

/**
 * User actions for transaction list screen.
 */
sealed interface TransactionListEvent {

    /**
     * Updates selected member filter.
     * Null means all members.
     */
    data class OnMemberFilterSelected(val memberId: Long?) : TransactionListEvent

    /**
     * Updates selected wallet filter.
     * Null means all wallets.
     */
    data class OnWalletFilterSelected(val walletId: Long?) : TransactionListEvent

    /**
     * Toggles a tag in selected tag filters.
     */
    data class OnTagFilterToggled(val tagId: Long) : TransactionListEvent

    /**
     * Clears only selected tags.
     */
    data object OnAllTagsSelected : TransactionListEvent

    /**
     * Clears all filters.
     */
    data object OnClearFiltersClicked : TransactionListEvent

    data class OnApplyFilterClicked(
        val filter: TransactionListFilter
    ) : TransactionListEvent

    data class OnTypeFilterChanged(val type: TransactionType?) : TransactionListEvent

}


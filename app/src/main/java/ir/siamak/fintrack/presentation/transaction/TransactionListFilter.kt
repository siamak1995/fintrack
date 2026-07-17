package ir.siamak.fintrack.presentation.transaction

/**
 * Filter state for transaction list screen.
 *
 * Rules:
 * - null member id means all members
 * - null wallet id means all wallets
 * - empty tag ids means all tags
 * - selected tags are matched with OR logic in MVP
 */
data class TransactionListFilter(
    val selectedMemberId: Long? = null,
    val selectedWalletId: Long? = null,
    val selectedTagIds: Set<Long> = emptySet()
) {
    /**
     * Returns true when at least one filter is active.
     */
    val hasActiveFilters: Boolean
        get() = selectedMemberId != null ||
                selectedWalletId != null ||
                selectedTagIds.isNotEmpty()
}

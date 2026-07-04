package ir.siamak.fintrack.domain.report.usecases

import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.report.model.FilteredTransactionResult
import ir.siamak.fintrack.domain.repository.MemberRepository
import ir.siamak.fintrack.domain.repository.TransactionRepository
import ir.siamak.fintrack.domain.repository.WalletRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetFilteredTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(
        startTimestamp: Long?,
        endTimestamp: Long?,
        memberId: Long?,
        walletId: Long?,
        transactionType: TransactionType?
    ): List<FilteredTransactionResult> {
        // ۱. دریافت داده‌های پایه به صورت همزمان
        val transactions = transactionRepository.getAllTransactionsSync()
        val wallets = walletRepository.getAllWallets().first().associateBy { it.id }
        val members = memberRepository.getAllMembers().first().associateBy { it.id }

        // ۲. اعمال فیلترهای پویا و ترکیبی
        return transactions.filter { tx ->
            val matchesStart = startTimestamp == null || tx.date >= startTimestamp
            val matchesEnd = endTimestamp == null || tx.date <= endTimestamp
            val matchesMember = memberId == null || tx.memberId == memberId
            val matchesWallet = walletId == null || tx.walletId == walletId
            val matchesType = transactionType == null || tx.type == transactionType

            matchesStart && matchesEnd && matchesMember && matchesWallet && matchesType
        }.map { tx ->
            val wallet = wallets[tx.walletId]
            val member = members[tx.memberId]

            FilteredTransactionResult(
                transaction = tx,
                walletName = wallet?.name ?: "بدون حساب",
                walletColor = wallet?.color ?: "#7F8C8D",
                memberName = member?.name ?: "بدون عضو",
                memberColor = member?.color ?: "#7F8C8D"
            )
        }.sortedByDescending { it.transaction.date } // نمایش از جدیدترین به قدیمی‌ترین
    }
}

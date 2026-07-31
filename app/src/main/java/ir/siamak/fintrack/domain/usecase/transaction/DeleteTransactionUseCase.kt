package ir.siamak.fintrack.domain.usecase.transaction

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.repository.TransactionRepository
import ir.siamak.fintrack.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * تراکنش را حذف کرده و اثر مالی آن را از موجودی کیف پول‌ها برمی‌گرداند.
 */
class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    /**
     * اثر مالی تراکنش را معکوس کرده و سپس تراکنش را حذف می‌کند.
     */
    suspend operator fun invoke(transaction: Transaction) {
        val persistedTransaction = transactionRepository.getTransactionById(transaction.id)
            ?: throw IllegalArgumentException("تراکنش موردنظر پیدا نشد.")

        val reversedBalances = when (persistedTransaction.type) {
            TransactionType.INCOME -> {
                val wallet = getWallet(persistedTransaction.walletId)
                val newBalance = wallet.balance - persistedTransaction.amount

                if (newBalance < 0.0) {
                    throw IllegalStateException(
                        "موجودی کیف پول «${wallet.name}» برای حذف این درآمد کافی نیست."
                    )
                }

                listOf(wallet.copy(balance = newBalance))
            }

            TransactionType.EXPENSE -> {
                val wallet = getWallet(persistedTransaction.walletId)

                listOf(
                    wallet.copy(
                        balance = wallet.balance + persistedTransaction.amount
                    )
                )
            }

            TransactionType.TRANSFER -> {
                val targetWalletId = persistedTransaction.toWalletId
                    ?: throw IllegalArgumentException("کیف پول مقصد تراکنش پیدا نشد.")

                val sourceWallet = getWallet(persistedTransaction.walletId)
                val targetWallet = getWallet(targetWalletId)
                val targetBalance = targetWallet.balance - persistedTransaction.amount

                if (targetBalance < 0.0) {
                    throw IllegalStateException(
                        "موجودی کیف پول مقصد «${targetWallet.name}» برای حذف این انتقال کافی نیست."
                    )
                }

                listOf(
                    sourceWallet.copy(
                        balance = sourceWallet.balance + persistedTransaction.amount
                    ),
                    targetWallet.copy(balance = targetBalance)
                )
            }
        }

        reversedBalances.forEach { wallet ->
            walletRepository.update(wallet)
        }

        transactionRepository.deleteTransaction(persistedTransaction)
    }

    private suspend fun getWallet(walletId: Long) =
        walletRepository.getById(walletId)
            ?: throw IllegalArgumentException("کیف پول مرتبط با تراکنش پیدا نشد.")
}

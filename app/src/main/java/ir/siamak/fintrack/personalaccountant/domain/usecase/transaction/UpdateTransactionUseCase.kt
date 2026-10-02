package ir.siamak.fintrack.personalaccountant.domain.usecase.transaction

import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.domain.repository.TransactionRepository
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * تراکنش موجود را ویرایش کرده و اثر مالی نسخه قبلی و جدید را روی کیف پول‌ها اعمال می‌کند.
 */
class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    /**
     * ابتدا اثر تراکنش قبلی را حذف و سپس اثر تراکنش جدید را اعمال می‌کند.
     */
    suspend operator fun invoke(transaction: Transaction) {
        require(transaction.id > 0L) {
            "شناسه تراکنش نامعتبر است."
        }

        val previousTransaction = transactionRepository.getTransactionById(transaction.id)
            ?: throw IllegalArgumentException("تراکنش موردنظر پیدا نشد.")

        val balanceChanges = mutableMapOf<Long, Double>()

        previousTransaction.balanceEffects().forEach { (walletId, amount) ->
            balanceChanges[walletId] = balanceChanges.getOrDefault(walletId, 0.0) - amount
        }

        transaction.balanceEffects().forEach { (walletId, amount) ->
            balanceChanges[walletId] = balanceChanges.getOrDefault(walletId, 0.0) + amount
        }

        val updatedWallets = balanceChanges
            .filterValues { it != 0.0 }
            .map { (walletId, balanceChange) ->
                val wallet = walletRepository.getById(walletId)
                    ?: throw IllegalArgumentException("کیف پول مرتبط با تراکنش پیدا نشد.")

                val updatedBalance = wallet.balance + balanceChange

                if (updatedBalance < 0.0) {
                    throw IllegalStateException(
                        "موجودی کیف پول «${wallet.name}» برای این تغییر کافی نیست."
                    )
                }

                wallet.copy(balance = updatedBalance)
            }

        updatedWallets.forEach { wallet ->
            walletRepository.update(wallet)
        }

        transactionRepository.updateTransaction(transaction)
    }

    /**
     * اثر مالی تراکنش روی هر کیف پول را محاسبه می‌کند.
     *
     * مقدار مثبت به معنی افزایش و مقدار منفی به معنی کاهش موجودی است.
     */
    private fun Transaction.balanceEffects(): Map<Long, Double> {
        return when (type) {
            TransactionType.INCOME -> mapOf(walletId to amount)
            TransactionType.EXPENSE -> mapOf(walletId to -amount)
            TransactionType.TRANSFER -> {
                val targetWalletId = toWalletId
                    ?: throw IllegalArgumentException("کیف پول مقصد انتخاب نشده است.")

                require(walletId != targetWalletId) {
                    "کیف پول مبدا و مقصد نمی‌توانند یکسان باشند."
                }

                mapOf(
                    walletId to -amount,
                    targetWalletId to amount
                )
            }
        }
    }
}


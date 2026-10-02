package ir.siamak.fintrack.personalaccountant.domain.usecase.transaction

import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.domain.repository.TransactionRepository
import ir.siamak.fintrack.personalaccountant.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * ثبت تراکنش جدید.
 *
 * مسئولیت این UseCase فقط ذخیره Transaction نیست.
 *
 * قبل از ذخیره تراکنش باید موجودی Wallet نیز
 * بروزرسانی شود تا Dashboard و Reports
 * همیشه اطلاعات صحیح داشته باشند.
 */
class InsertTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    /**
     * ثبت تراکنش جدید.
     */
    suspend operator fun invoke(transaction: Transaction) {
        val sourceWallet = walletRepository.getById(transaction.walletId)
            ?: throw IllegalArgumentException("کیف پول مبدا پیدا نشد.")

        when (transaction.type) {
            TransactionType.INCOME -> {
                val newBalance = sourceWallet.balance + transaction.amount
                walletRepository.update(sourceWallet.copy(balance = newBalance))
            }

            TransactionType.EXPENSE -> {
                if (sourceWallet.balance < transaction.amount) {
                    throw IllegalStateException("موجودی حساب مبدا کافی نیست.")
                }
                val newBalance = sourceWallet.balance - transaction.amount
                walletRepository.update(sourceWallet.copy(balance = newBalance))
            }

            TransactionType.TRANSFER -> {
                val targetWalletId = transaction.toWalletId
                    ?: throw IllegalArgumentException("کیف پول مقصد انتخاب نشده است.")

                if (transaction.walletId == targetWalletId) {
                    throw IllegalArgumentException("کیف پول مبدا و مقصد نمی‌توانند یکسان باشند.")
                }

                val targetWallet = walletRepository.getById(targetWalletId)
                    ?: throw IllegalArgumentException("کیف پول مقصد پیدا نشد.")

                if (sourceWallet.balance < transaction.amount) {
                    throw IllegalStateException("موجودی حساب مبدا برای انتقال کافی نیست.")
                }

                // کسر از مبدا و افزودن به مقصد
                walletRepository.update(sourceWallet.copy(balance = sourceWallet.balance - transaction.amount))
                walletRepository.update(targetWallet.copy(balance = targetWallet.balance + transaction.amount))
            }
        }

        // ذخیره خود تراکنش در تاریخچه دیتابیس
        transactionRepository.insertTransaction(transaction)
    }
}

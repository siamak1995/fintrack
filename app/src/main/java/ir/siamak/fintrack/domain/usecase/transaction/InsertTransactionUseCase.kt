package ir.siamak.fintrack.domain.usecase.transaction

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.repository.TransactionRepository
import ir.siamak.fintrack.domain.repository.WalletRepository
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

        val wallet =
            walletRepository.getById(transaction.walletId)
                ?: throw IllegalArgumentException("کیف پول پیدا نشد.")

        val newBalance = when (transaction.type) {

            TransactionType.INCOME ->
                wallet.balance + transaction.amount

            TransactionType.EXPENSE ->
                wallet.balance - transaction.amount

            TransactionType.TRANSFER ->
                wallet.balance

        }

        walletRepository.update(

            wallet.copy(
                balance = newBalance
            )

        )

        transactionRepository.insert(transaction)

    }

}
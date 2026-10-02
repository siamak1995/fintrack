package ir.siamak.fintrack.personalaccountant.domain.usecase.transaction

data class TransactionUseCases(
    val getAllTransactions: GetAllTransactionsUseCase,
    val getTransactionsByWallet: GetTransactionsByWalletUseCase,
    val insertTransaction: InsertTransactionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase,
    val getTransactionById: GetTransactionByIdUseCase
)


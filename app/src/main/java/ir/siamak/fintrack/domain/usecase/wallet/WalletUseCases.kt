package ir.siamak.fintrack.domain.usecase.wallet

/**
 * کلاس تجمیعی یوزکیس‌های مرتبط با Wallet.
 *
 * این الگو باعث می‌شود به جای تزریق چندین UseCase به ViewModel،
 * همه آن‌ها در قالب یک آبجکت واحد تزریق شوند.
 */
data class WalletUseCases(
    val getAllWallets: GetAllWalletsUseCase,
    val getWalletById: GetWalletByIdUseCase,
    val insertWallet: InsertWalletUseCase,
    val deleteWallet: DeleteWalletUseCase
)
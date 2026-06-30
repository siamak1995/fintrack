package ir.siamak.fintrack.presentation.installment.add_edit_installment

sealed interface AddEditInstallmentEvent {

    data class TitleChanged(
        val value: String
    ) : AddEditInstallmentEvent

    data class TotalAmountChanged(
        val value: String
    ) : AddEditInstallmentEvent

    data class PaidAmountChanged(
        val value: String
    ) : AddEditInstallmentEvent

    data class DueDateChanged(
        val value: Long
    ) : AddEditInstallmentEvent

    data object Save : AddEditInstallmentEvent

    data class WalletChanged(
        val walletId: Long
    ) : AddEditInstallmentEvent

    data class NoteChanged(
        val note: String
    ) : AddEditInstallmentEvent

}
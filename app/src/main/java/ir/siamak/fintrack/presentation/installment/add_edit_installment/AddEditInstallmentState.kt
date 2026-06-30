package ir.siamak.fintrack.presentation.installment.add_edit_installment

import ir.siamak.fintrack.data.model.Wallet

data class AddEditInstallmentState(

    val title: String = "",

    val totalAmount: String = "",

    val paidAmount: String = "",

    val dueDate: Long = System.currentTimeMillis(),

    val walletId: Long = 0L,

    val note: String = "",

    val isLoading: Boolean = false,

    val error: String? = null,

    val wallets: List<Wallet> = emptyList()

)
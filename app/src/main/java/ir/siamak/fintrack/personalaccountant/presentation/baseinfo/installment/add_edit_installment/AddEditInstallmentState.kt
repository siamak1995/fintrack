package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.add_edit_installment

import ir.siamak.fintrack.personalaccountant.data.model.Wallet

data class AddEditInstallmentState(

    val title: String = "",
    val installmentCountRaw: String = "1",
    val startDate: Long = System.currentTimeMillis(),
    val automaticPayment: Boolean = false,
    val isSaved: Boolean = false,

    val totalAmountFormatted: String = "",
    val totalAmountRaw: String = "",


    val paidAmountFormatted: String = "",
    val paidAmountRaw: String = "",

    val dueDate: Long = System.currentTimeMillis(),

    val walletId: Long = 0L,

    val note: String = "",

    val isLoading: Boolean = false,

    val error: String? = null,

    val wallets: List<Wallet> = emptyList(),

    val paidExceedsTotal: Boolean = false

)

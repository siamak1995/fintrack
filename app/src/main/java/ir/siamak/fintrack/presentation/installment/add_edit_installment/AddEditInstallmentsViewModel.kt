package ir.siamak.fintrack.presentation.installment.add_edit_installment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.domain.usecase.installments.InstallmentUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel صفحه افزودن/ویرایش قسط
 *
 * مسئول:
 * - مدیریت State
 * - اعتبارسنجی اطلاعات
 * - ثبت قسط
 */
@HiltViewModel
class AddEditInstallmentsViewModel @Inject constructor(
    private val installmentUseCases: InstallmentUseCases,
    private val walletUseCases: WalletUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditInstallmentState())

    val state = _state.asStateFlow()

    init {
        loadWallets()
    }

    /**
     * دریافت Eventهای UI
     */
    fun onEvent(event: AddEditInstallmentEvent) {

        when (event) {

            is AddEditInstallmentEvent.TitleChanged -> {

                _state.update {

                    it.copy(
                        title = event.value
                    )

                }

            }

            is AddEditInstallmentEvent.WalletChanged -> {

                _state.update {

                    it.copy(
                        walletId = event.walletId
                    )

                }

            }

            is AddEditInstallmentEvent.NoteChanged -> {

                _state.update {

                    it.copy(
                        note = event.note
                    )

                }

            }

            is AddEditInstallmentEvent.TotalAmountChanged -> {

                _state.update {

                    it.copy(
                        totalAmount = event.value
                    )

                }

            }

            is AddEditInstallmentEvent.PaidAmountChanged -> {

                _state.update {

                    it.copy(
                        paidAmount = event.value
                    )

                }

            }

            is AddEditInstallmentEvent.DueDateChanged -> {

                _state.update {

                    it.copy(
                        dueDate = event.value
                    )

                }

            }

            AddEditInstallmentEvent.Save -> {

                saveInstallment()

            }
        }
    }

    /**
     * ذخیره قسط
     */
    private fun saveInstallment() {

        val state = _state.value

        if (state.title.isBlank()) {

            _state.update {

                it.copy(
                    error = "عنوان قسط را وارد کنید."
                )

            }

            return
        }

        val total = state.totalAmount.toDoubleOrNull()

        if (total == null || total <= 0) {

            _state.update {

                it.copy(
                    error = "مبلغ کل معتبر نیست."
                )

            }

            return
        }

        val paid = state.paidAmount.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {

            _state.update {

                it.copy(
                    isLoading = true,
                    error = null
                )

            }

            installmentUseCases.insertInstallment(

                Installment(

                    id = 0,

                    title = state.title,

                    totalAmount = total,

                    paidAmount = paid,

                    dueDate = state.dueDate,

                    createdAt = System.currentTimeMillis(),

                    note = state.note.ifBlank { null },

                    walletId = state.walletId,

                    isPaid = paid >= total

                )

            )

            _state.update {

                it.copy(
                    isLoading = false
                )

            }

        }

    }


    private fun loadWallets() {

        viewModelScope.launch {

            walletUseCases.getAllWallets()

                .collect { wallets ->

                    _state.update {

                        it.copy(

                            wallets = wallets,

                            walletId =
                                if (it.walletId == 0L && wallets.isNotEmpty())
                                    wallets.first().id
                                else
                                    it.walletId

                        )

                    }

                }

        }

    }
}
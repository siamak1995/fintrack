package ir.siamak.fintrack.presentation.report.pages.wallet

import ir.siamak.fintrack.core.datepicker.model.PersianDate

data class WalletReportUiState(
    val selectedFromDate: PersianDate? = null,
    val selectedToDate: PersianDate? = null,
    val items: List<WalletReportUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDatePickerVisible: Boolean = false
)

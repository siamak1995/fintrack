package ir.siamak.fintrack.presentation.report.pages.filtered

import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.report.model.FilteredTransactionResult

data class FilteredReportUiState(
    // لیست کلیدها جهت منوهای بازشو (Dropdowns)
    val wallets: List<Wallet> = emptyList(),
    val members: List<Member> = emptyList(),

    // مقادیر فیلترهای انتخابی فعلی
    val selectedFromDate: PersianDate? = null,
    val selectedToDate: PersianDate? = null,
    val selectedMemberId: Long? = null, // null یعنی همه اعضا
    val selectedWalletId: Long? = null, // null یعنی همه حساب‌ها
    val selectedType: TransactionType? = null, // null یعنی همه انواع

    // نتایج و وضعیت لودینگ
    val transactions: List<FilteredReportUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val errorMessage: String? = null
)

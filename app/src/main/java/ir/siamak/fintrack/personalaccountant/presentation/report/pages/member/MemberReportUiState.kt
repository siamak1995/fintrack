package ir.siamak.fintrack.personalaccountant.presentation.report.pages.member

import ir.siamak.fintrack.common.core.datepicker.model.PersianDate

/**
 * UI state of Member Report screen.
 *
 * @property selectedFromDate تاریخ شروع انتخاب‌شده توسط کاربر
 * @property selectedToDate تاریخ پایان انتخاب‌شده توسط کاربر
 * @property items لیست گزارش اعضا
 * @property isLoading وضعیت بارگذاری
 * @property errorMessage پیام خطا در صورت بروز مشکل
 * @property isDatePickerVisible وضعیت نمایش دیالوگ/شیت انتخاب بازه تاریخ
 */
data class MemberReportUiState(
    val selectedFromDate: PersianDate? = null,
    val selectedToDate: PersianDate? = null,
    val items: List<MemberReportUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDatePickerVisible: Boolean = false,
)


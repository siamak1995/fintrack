package ir.siamak.fintrack.presentation.report.pages.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.domain.model.MemberReport
import ir.siamak.fintrack.domain.usecase.report.GetMemberReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

/**
 * Use case contract for loading member reports.
 *
 * این قرارداد باید با UseCase واقعی پروژه جایگزین یا منطبق شود.
 * خروجی آن باید گزارش تجمیع‌شده هر عضو را بر اساس بازه زمانی بدهد.
 */
fun interface GetMemberReportUseCase {
    suspend operator fun invoke(
        fromDate: LocalDate?,
        toDate: LocalDate?,
    ): List<MemberReport>
}

@HiltViewModel
class MemberReportViewModel @Inject constructor(
    private val getMemberReportUseCase: GetMemberReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberReportUiState())
    val uiState: StateFlow<MemberReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    /**
     * نمایش انتخاب‌گر بازه تاریخ
     */
    fun showDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = true) }
    }

    /**
     * بستن انتخاب‌گر بازه تاریخ
     */
    fun hideDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    /**
     * پاک کردن بازه انتخابی و بازخوانی گزارش
     *
     * وقتی هیچ تاریخی انتخاب نشده باشد، منطق سیستم باید
     * از ابتدای داده‌ها تا امروز را لحاظ کند.
     */
    fun clearDateRange() {
        _uiState.update { it.copy(selectedFromDate = null, selectedToDate = null) }
        loadReport()
    }

    /**
     * ثبت بازه انتخاب‌شده و بارگذاری مجدد گزارش
     */
    fun onDateRangeSelected(fromDate: PersianDate?, toDate: PersianDate?) {
        _uiState.update {
            it.copy(selectedFromDate = fromDate, selectedToDate = toDate, isDatePickerVisible = false)
        }
        loadReport()
    }

    /**
     * دریافت گزارش از منبع واقعی داده
     */
    private fun loadReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // تبدیل تاریخ شمسی به Timestamp برای کوئری Room
                val startTimestamp = _uiState.value.selectedFromDate?.toTimestamp(isStartOfDay = true)
                val endTimestamp = _uiState.value.selectedToDate?.toTimestamp(isStartOfDay = false)

                val reports = getMemberReportUseCase(startTimestamp, endTimestamp)

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        items = reports.map { it.toUiModel() }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "خطا در بارگذاری: ${e.localizedMessage}") }
            }
        }

    }
}

/**
 * Converts PersianDate to LocalDate.
 *
 * نکته:
 * این بخش باید با توجه به پیاده‌سازی واقعی PersianDate در پروژه تو تنظیم شود.
 * اگر PersianDate از قبل تابع تبدیل دارد، همین را حذف و از تابع اصلی استفاده کن.
 */
private fun PersianDate.toTimestamp(isStartOfDay: Boolean): Long {
    // توجه: برای دقت ۱۰۰٪ باید از کتابخانه تبدیل تاریخ (مثل JDF) استفاده کنی.
    // فعلاً از یک تبدیل تخمینی یا مستقیم (اگر تاریخ‌ها میلادی ذخیره می‌شوند) استفاده می‌کنیم.
    val calendar = Calendar.getInstance()
    // فرض بر این است که تاریخ‌های دیتابیس میلادی هستند.
    // اگر شمسی ذخیره می‌کنی، تبدیل لازم نیست.
    // اینجا کد تبدیل شمسی به میلادی را باید صدا بزنی.
    calendar.set(year, month - 1, day)
    if (isStartOfDay) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
    } else {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
    }
    return calendar.timeInMillis
}

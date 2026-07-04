package ir.siamak.fintrack.presentation.report.pages.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.core.datepicker.model.PersianDate
import ir.siamak.fintrack.domain.report.usecases.GetMemberReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * ViewModel صفحه گزارش اعضا.
 *
 * این کلاس مسئول:
 * - نگهداری state صفحه
 * - باز و بسته کردن دیالوگ انتخاب بازه تاریخ
 * - ثبت بازه انتخابی کاربر
 * - تبدیل تاریخ شمسی به timestamp قابل استفاده در دیتابیس Room
 * - فراخوانی UseCase برای دریافت گزارش اعضا
 * - مدیریت loading و error state
 *
 * ساختار فیلتر تاریخ:
 * - selectedFromDate -> ابتدای روز (00:00:00.000)
 * - selectedToDate   -> انتهای روز (23:59:59.999)
 *
 * دلیل این کار:
 * تا وقتی کاربر یک روز را به عنوان پایان بازه انتخاب می‌کند،
 * تمام تراکنش‌های همان روز هم داخل نتیجه باشند.
 */
@HiltViewModel
class MemberReportViewModel @Inject constructor(
    private val getMemberReportUseCase: GetMemberReportUseCase
) : ViewModel() {

    /**
     * state داخلی قابل تغییر.
     */
    private val _uiState = MutableStateFlow(MemberReportUiState())

    /**
     * state قابل مشاهده برای UI.
     */
    val uiState: StateFlow<MemberReportUiState> = _uiState.asStateFlow()

    /**
     * در شروع ViewModel، گزارش اولیه بارگذاری می‌شود.
     */
    init {
        loadReport()
    }

    /**
     * نمایش دیالوگ انتخاب بازه تاریخ.
     */
    fun showDatePicker() {
        _uiState.update { currentState ->
            currentState.copy(isDatePickerVisible = true)
        }
    }

    /**
     * بستن دیالوگ انتخاب بازه تاریخ.
     */
    fun hideDatePicker() {
        _uiState.update { currentState ->
            currentState.copy(isDatePickerVisible = false)
        }
    }

    /**
     * پاک کردن بازه انتخاب‌شده و بارگذاری مجدد گزارش.
     *
     * در این حالت فیلتر تاریخ حذف می‌شود و گزارش با کل داده‌ها
     * یا منطق پیش‌فرض UseCase نمایش داده خواهد شد.
     */
    fun clearDateRange() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedFromDate = null,
                selectedToDate = null,
                isDatePickerVisible = false
            )
        }
        loadReport()
    }

    /**
     * ثبت بازه انتخاب‌شده توسط کاربر و بارگذاری مجدد گزارش.
     *
     * اگر کاربر تاریخ‌ها را برعکس انتخاب کرده باشد،
     * بازه قبل از ذخیره در state نرمال‌سازی می‌شود.
     *
     * @param fromDate تاریخ شروع شمسی
     * @param toDate تاریخ پایان شمسی
     */
    fun onDateRangeSelected(fromDate: PersianDate?, toDate: PersianDate?) {
        val (normalizedFrom, normalizedTo) = normalizeDateRange(fromDate, toDate)

        _uiState.update { currentState ->
            currentState.copy(
                selectedFromDate = normalizedFrom,
                selectedToDate = normalizedTo,
                isDatePickerVisible = false
            )
        }

        loadReport()
    }

    /**
     * دریافت گزارش اعضا از UseCase و به‌روزرسانی state صفحه.
     *
     * مراحل:
     * 1. فعال‌سازی loading
     * 2. تبدیل تاریخ‌های شمسی انتخاب‌شده به timestamp
     * 3. ارسال بازه به UseCase
     * 4. تبدیل خروجی دامنه به مدل UI
     * 5. ثبت نتیجه در state
     *
     * در صورت بروز خطا، پیام خطا داخل state ذخیره می‌شود.
     */
    private fun loadReport() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val currentState = _uiState.value

                val startTimestamp = currentState.selectedFromDate?.toStartOfDayTimestamp()
                val endTimestamp = currentState.selectedToDate?.toEndOfDayTimestamp()

                val reports = getMemberReportUseCase(
                    startTimestamp,
                    endTimestamp
                )

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        items = reports.map { report -> report.toUiModel() }
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage
                            ?: "خطا در بارگذاری گزارش اعضا"
                    )
                }
            }
        }
    }

    /**
     * نرمال‌سازی بازه تاریخ.
     *
     * اگر هر دو تاریخ موجود باشند و کاربر بازه را برعکس انتخاب کرده باشد،
     * تاریخ‌ها با هم جابه‌جا می‌شوند تا همیشه:
     *
     * fromDate <= toDate
     *
     * برقرار باشد.
     *
     * @param fromDate تاریخ شروع
     * @param toDate تاریخ پایان
     * @return بازه نرمال‌شده به صورت Pair<from, to>
     */
    private fun normalizeDateRange(
        fromDate: PersianDate?,
        toDate: PersianDate?
    ): Pair<PersianDate?, PersianDate?> {
        if (fromDate == null || toDate == null) {
            return fromDate to toDate
        }

        return if (fromDate.isBeforeOrEqual(toDate)) {
            fromDate to toDate
        } else {
            toDate to fromDate
        }
    }
}

/**
 * تبدیل تاریخ شمسی به timestamp ابتدای روز.
 *
 * این مقدار برای شروع بازه استفاده می‌شود تا تمام رخدادهای آن روز
 * از ساعت 00:00:00.000 به بعد شامل شوند.
 *
 * @receiver تاریخ شمسی
 * @return timestamp معادل ابتدای روز بر اساس timezone سیستم
 */
private fun PersianDate.toStartOfDayTimestamp(): Long {
    val gregorianDate = this.toGregorianLocalDate()

    return gregorianDate
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

/**
 * تبدیل تاریخ شمسی به timestamp انتهای روز.
 *
 * این مقدار برای پایان بازه استفاده می‌شود تا تمام رخدادهای آن روز
 * تا ساعت 23:59:59.999 داخل نتیجه باقی بمانند.
 *
 * @receiver تاریخ شمسی
 * @return timestamp معادل انتهای روز بر اساس timezone سیستم
 */
private fun PersianDate.toEndOfDayTimestamp(): Long {
    val gregorianDate = this.toGregorianLocalDate()

    return gregorianDate
        .atTime(LocalTime.of(23, 59, 59, 999_000_000))
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

/**
 * تبدیل یک تاریخ شمسی به LocalDate میلادی.
 *
 * این تابع از converter اصلی پروژه استفاده می‌کند.
 *
 * نکته:
 * اگر نام متد converter در پروژه تو متفاوت است،
 * فقط همین بخش را با متد واقعی جایگزین کن.
 *
 * مثال‌های محتمل:
 * - JalaliDateConverter.toGregorian(this)
 * - JalaliDateConverter.toGregorian(year, month, day)
 * - JalaliDateConverter.toLocalDate(this)
 *
 * @receiver تاریخ شمسی
 * @return تاریخ میلادی متناظر به صورت LocalDate
 */
private fun PersianDate.toGregorianLocalDate(): LocalDate {
    JalaliCalendarEngine.requireValidDate(this)

    return JalaliDateConverter.toGregorian(this)
}

/**
 * بررسی می‌کند آیا تاریخ فعلی قبل از یا مساوی تاریخ دیگر است یا نه.
 *
 * این تابع برای نرمال‌سازی بازه و جلوگیری از معکوس شدن
 * start و end استفاده می‌شود.
 *
 * @param other تاریخ مقصد برای مقایسه
 * @return
 * true اگر این تاریخ قبل از یا مساوی [other] باشد
 * false در غیر این صورت
 */
private fun PersianDate.isBeforeOrEqual(other: PersianDate): Boolean {
    return when {
        year != other.year -> year < other.year
        month != other.month -> month < other.month
        else -> day <= other.day
    }
}

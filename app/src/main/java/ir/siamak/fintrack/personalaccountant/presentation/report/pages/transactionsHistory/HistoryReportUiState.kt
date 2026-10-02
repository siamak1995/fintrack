package ir.siamak.fintrack.personalaccountant.presentation.report.pages.transactionsHistory

import ir.siamak.fintrack.common.core.datepicker.model.PersianDate

/**
 * نوع فیلتر تراکنش برای گزارش تاریخچه.
 */
enum class HistoryTransactionTypeFilter {
    ALL,
    INCOME,
    EXPENSE,
    TRANSFER
}

/**
 * مدل نمایشی ساده برای تگ.
 *
 * این مدل فقط برای لایه UI استفاده می‌شود تا صفحه تاریخچه
 * وابستگی مستقیم کمتری به مدل‌های دیتابیس یا دامنه داشته باشد.
 */
data class HistoryTagUiModel(
    val id: Long,
    val name: String,
    val color: Long?
)


/**
 * مدل نمایشی تراکنش برای صفحه تاریخچه.
 */
data class HistoryTransactionUiModel(
    val id: Long,
    val title: String,
    val note: String?,
    val amount: Long,
    val type: HistoryTransactionTypeFilter,
    val dateText: String,
    val walletName: String,
    val categoryName: String?,
    val tags: List<HistoryTagUiModel>
)

/**
 * مدل داده برای نمودار بالای صفحه.
 *
 * @property label نام حساب/بانک
 * @property value مقدار اصلی ستون
 */
data class HistoryChartItemUiModel(
    val label: String,
    val value: Float
)

/**
 * مدل خلاصه بالای صفحه.
 *
 * در حالت فیلتر نوع، فقط یک جمع اصلی نمایش می‌دهیم.
 * در حالت فیلتر ترکیبی یا بر اساس تگ/بازه، جمع درآمد و هزینه
 * هم‌زمان نمایش داده می‌شود.
 */
data class HistorySummaryUiState(
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val totalTransfer: Long = 0L,
    val netBalance: Long = 0L
)

/**
 * وضعیت کامل UI برای صفحه گزارش تاریخچه تراکنش‌ها.
 */
data class HistoryReportUiState(
    val selectedFromDate: PersianDate? = null,
    val selectedToDate: PersianDate? = null,
    val selectedType: HistoryTransactionTypeFilter = HistoryTransactionTypeFilter.ALL,
    val selectedTagIds: Set<Long> = emptySet(),
    val availableTags: List<HistoryTagUiModel> = emptyList(),
    val summary: HistorySummaryUiState = HistorySummaryUiState(),
    val chartItems: List<HistoryChartItemUiModel> = emptyList(),
    val transactions: List<HistoryTransactionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDatePickerVisible: Boolean = false
)


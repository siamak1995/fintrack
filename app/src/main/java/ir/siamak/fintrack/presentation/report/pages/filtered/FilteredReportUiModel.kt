package ir.siamak.fintrack.presentation.report.pages.filtered

import androidx.compose.ui.graphics.Color
import ir.siamak.fintrack.data.model.TransactionType

/**
 * مدل آماده نمایش برای هر ردیف گزارش فیلترشده.
 *
 * این مدل باعث می‌شود UI به مدل دیتابیس یا مدل دامنه وابسته نباشد
 * و متن‌ها، رنگ‌ها و فرمت‌های نمایشی در ViewModel آماده شوند.
 */
data class FilteredReportUiModel(
    val id: Long,
    val title: String,
    val note: String,
    val amountText: String,
    val amountColor: Color,
    val type: TransactionType,
    val typeLabel: String,
    val typeColor: Color,
    val walletName: String,
    val walletColor: Color,
    val memberName: String,
    val memberColor: Color,
    val dateText: String,
    val signedAmount: Long
)

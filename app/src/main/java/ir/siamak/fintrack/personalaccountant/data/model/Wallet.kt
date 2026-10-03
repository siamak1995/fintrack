package ir.siamak.fintrack.personalaccountant.data.model

import ir.siamak.fintrack.personalaccountant.domain.context.LEGACY_PERSONAL_CONTEXT_ID


/**
 * نمایش‌دهنده یک حساب یا کیف پول
 * @param id شناسه منحصر به فرد
 * @param name نام حساب (مثلاً: بانک ملی، جیب شخصی)
 * @param balance موجودی فعلی
 * @param color کد رنگ برای نمایش گرافیکی در UI
 */
data class Wallet(
    val id: Long = 0L,
    val contextId: Long = LEGACY_PERSONAL_CONTEXT_ID,
    val name: String,
    val balance: Double,
    val color: String,
    val iconRes: String? = null,
    val currency: Currency = Currency.TOMAN
)


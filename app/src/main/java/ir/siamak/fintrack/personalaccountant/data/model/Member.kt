package ir.siamak.fintrack.personalaccountant.data.model

import ir.siamak.fintrack.personalaccountant.domain.context.LEGACY_PERSONAL_CONTEXT_ID

/**
 * مدل مربوط به اعضای خانواده.
 *
 * @property id شناسه یکتای عضو.
 * @property name نام عضو (مثلاً: سیامک، همسر).
 * @property avatarRes آیکون یا تصویر پروفایل (فعلاً به صورت نام ریسورس یا استرینگ).
 */
data class Member(
    val id: Long = 0L,
    val contextId: Long = LEGACY_PERSONAL_CONTEXT_ID,
    val name: String,
    val relation: String,
    val color: String,
    val icon: String = "ic_default_user"
)


package ir.siamak.fintrack.personalaccountant.banksms

import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable enum class BankSmsKind { INCOME, EXPENSE, TRANSFER }
@Serializable enum class SmsCurrency { RIAL, TOMAN }
@Serializable data class ParsedBankSms(val amount: String?, val currency: SmsCurrency?, val kind: BankSmsKind?,
    val accountHints: List<String> = emptyList(), val incoming: Boolean? = null,
    val balance: String? = null, val balanceCurrency: SmsCurrency? = null, val bankName: String? = null,
    val occurredAt: Long? = null)

object BankSmsParser {
    /** Automatic intake is deliberately stricter than manual extraction. */
    fun isBankTransaction(body: String, parsed: ParsedBankSms? = parse(body)): Boolean {
        if(parsed?.amount == null) return false
        val text = normalized(body)
        if(Regex("تخفیف|جشنواره|قرعه\\s*کشی|پیشنهاد\\s*ویژه|کد\\s*هدیه|لغو\\s*(?:ارسال|پیامک|11)|برای\\s*(?:خرید|پرداخت|واریز)|جهت\\s*(?:خرید|پرداخت|واریز)|ناموفق|انجام\\s*نشد|عدم\\s*موفقیت").containsMatchIn(text)) return false
        // A price or a bank name in an advertisement is not evidence of an account operation.
        return parsed.accountHints.isNotEmpty() || parsed.balance != null ||
            (parsed.bankName != null && Regex("انجام\\s*شد|انجام\\s*گردید|موفق|واریز\\s*شد|برداشت\\s*شد|از حساب شما پرید").containsMatchIn(text))
    }

    fun fingerprint(body: String): String = MessageDigest.getInstance("SHA-256")
        .digest(PersianNumberFormatter.normalize(body).trim().replace(Regex("\\s+"), " ").toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }

    private const val NUMBER = "[0-9]+(?:,[0-9]{3})*(?:\\.[0-9]+)?"
    private fun normalized(body: String) = PersianNumberFormatter.normalize(body)
        .replace('ي','ی').replace('ك','ک').replace('\u200c',' ')
        .replace(Regex("[\\u200e\\u200f\\u202a-\\u202e\\u2066-\\u2069\\ufeff]"), "")
        .replace('−','-').replace('＋','+')
    private fun unit(value: String?) = when(value) { "ریال" -> SmsCurrency.RIAL; "تومان" -> SmsCurrency.TOMAN; else -> null }
    private fun number(value: String?) = value?.replace(",", "")?.takeIf { it.toBigDecimalOrNull()?.signum()?.let { n -> n >= 0 } == true }

    /** Extract operation and balance independently; unknown units remain mandatory review fields. */
    fun parse(body: String): ParsedBankSms? {
        val text = normalized(body)
        if(!Regex("بانک|حساب|کارت|مانده|موجودی|ریال|تومان").containsMatchIn(text)) return null
        if(Regex("رمز\\s*(پویا|دوم|خرید|یک\\s*بار|یکبار)|کد\\s*(تایید|تأیید)").containsMatchIn(text)) return null
        val balanceMatch = Regex("(?:مانده|موجودی)(?:\\s*(?:حساب|کارت))?\\s*[:：=]?\\s*($NUMBER)\\s*(ریال|تومان)?").find(text)
        // Mask the whole balance clause before looking for a transaction amount.
        val operations = balanceMatch?.let { text.replaceRange(it.range, " ") } ?: text
        val signed = Regex("(?m)^\\s*(?:([+-])\\s*($NUMBER)|($NUMBER)\\s*([+-]))\\s*(ریال|تومان)?\\s*$").find(operations)
        val sign = signed?.let { it.groupValues[1].ifBlank { it.groupValues[4] } }
        val debit = Regex("برداشت|خرید|پرداخت|بدهکار|از حساب شما پرید").containsMatchIn(operations) || sign == "-"
        val credit = Regex("واریز|بستانکار").containsMatchIn(operations) || sign == "+"
        val transfer = Regex("انتقال|کارت\\s*به\\s*کارت|پایا|ساتنا").containsMatchIn(operations)
        if(!debit && !credit && !transfer) return null
        val kind = if(transfer) BankSmsKind.TRANSFER else if(credit && !debit) BankSmsKind.INCOME else if(debit && !credit) BankSmsKind.EXPENSE else null
        val labeled = Regex("(?:مبلغ|واریز|برداشت|خرید|پرداخت|بستانکار|بدهکار|انتقال\\s*وجه)\\s*[:：=]?\\s*($NUMBER)\\s*(ریال|تومان)?").find(operations)
        val beforeAction = Regex("($NUMBER)\\s*(ریال|تومان)\\s*(?:بابت|از حساب|به حساب|برداشت|واریز|خرید|پرداخت)").find(operations)
        val amount = number(labeled?.groupValues?.get(1) ?: signed?.let { it.groupValues[2].ifBlank { it.groupValues[3] } } ?: beforeAction?.groupValues?.get(1))?.takeIf { it.toBigDecimal().signum() > 0 }
        val explicitUnit = labeled?.groupValues?.get(2)?.ifBlank { null }
            ?: signed?.groupValues?.get(5)?.ifBlank { null } ?: beforeAction?.groupValues?.get(2)
        val currency = unit(explicitUnit ?: Regex("ریال|تومان").findAll(text).map { it.value }.distinct().singleOrNull())
        val hints = (Regex("(?:حساب|کارت)\\s*[:：]?\\s*([0-9*•.\\-]{4,40})").findAll(text)
            .map { it.groupValues[1].split(Regex("[^0-9]+")).lastOrNull { part -> part.isNotBlank() }.orEmpty() }
            + Regex("(?m)^\\s*([0-9]{10,26})\\s*$").findAll(text).map { it.groupValues[1] })
            .filter { it.length >= 4 }.distinct().toList()
        val bankName = if(Regex("(?:^|\\n)\\s*(?:بانک\\s*)?بلو\\s*(?:\\n|$)").containsMatchIn(text)) "بلو"
            else Regex("(?m)^\\s*بانک\\s+([^\\n\\r:：0-9]{2,35})\\s*$").find(text)?.groupValues?.get(1)?.trim()
        return ParsedBankSms(amount,currency,kind,hints,if(credit && !debit) true else if(debit && !credit) false else null,
            number(balanceMatch?.groupValues?.get(1)),unit(balanceMatch?.groupValues?.get(2)) ?: currency,bankName,parseTime(text))
    }

    private fun parseTime(text: String): Long? = runCatching {
        val date = Regex("(?<![0-9])(1[34][0-9]{2})[/.-]([0-9]{1,2})[/.-]([0-9]{1,2})(?![0-9])").find(text) ?: return null
        val time = Regex("(?<![0-9])([0-9]{1,2}):([0-9]{2})(?![0-9])").find(text) ?: return null
        val day = ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter.toGregorian(
            ir.siamak.fintrack.common.core.datepicker.model.PersianDate(date.groupValues[1].toInt(),date.groupValues[2].toInt(),date.groupValues[3].toInt()))
        day.atTime(time.groupValues[1].toInt(),time.groupValues[2].toInt()).atZone(java.time.ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli()
    }.getOrNull()
}

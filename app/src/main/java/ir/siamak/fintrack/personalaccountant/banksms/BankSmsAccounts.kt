package ir.siamak.fintrack.personalaccountant.banksms

import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter

object BankSmsAccounts {
    fun matches(parsed: ParsedBankSms, name: String, card: String?, account: String?): Boolean {
        fun digits(value: String) = PersianNumberFormatter.normalize(value).filter(Char::isDigit)
        if(parsed.accountHints.isNotEmpty()) return parsed.accountHints.any { hint ->
            listOfNotNull(card,account).any { digits(it).endsWith(hint) } ||
                (name.startsWith("حساب پیامک ") && digits(name).endsWith(hint))
        }
        val bank = parsed.bankName ?: return false
        return name.trim().removePrefix("بانک ") == bank.trim().removePrefix("بانک ")
    }
    fun suggestedName(parsed: ParsedBankSms): String = parsed.accountHints.firstOrNull()?.let { "حساب پیامک $it" }
        ?: parsed.bankName?.let { "بانک $it" }.orEmpty()
}

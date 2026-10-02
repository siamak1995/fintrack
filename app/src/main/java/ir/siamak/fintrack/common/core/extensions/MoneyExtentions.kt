package ir.siamak.fintrack.common.core.extensions

import ir.siamak.fintrack.personalaccountant.data.model.Currency

fun formatMoneyBySettings(
    amount: Double,
    currency: Currency
): String {
    val normalizedAmount = when (currency) {
        Currency.TOMAN -> amount
        Currency.RIAL -> amount * 10
        else -> amount
    }

    val suffix = when (currency) {
        Currency.TOMAN -> "تومان"
        Currency.RIAL -> "ریال"
        else -> currency.name
    }

    return "${normalizedAmount.toLong().formatWithSeparator()} $suffix"
}

private fun Long.formatWithSeparator(): String {
    return "%,d".format(this)
}


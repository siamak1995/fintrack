package ir.siamak.fintrack.core.extensions

import java.text.DecimalFormat

fun formatAmount(input: String): String {
    val clean = input.replace(",", "").filter { it.isDigit() }

    if (clean.isEmpty()) return ""

    val number = clean.toLong()

    val formatter = DecimalFormat("#,###")
    return formatter.format(number)
}
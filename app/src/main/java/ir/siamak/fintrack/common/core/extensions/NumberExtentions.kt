package ir.siamak.fintrack.common.core.extensions

import java.text.DecimalFormat

fun formatAmount(input: String): String {
    val clean = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(input).replace(",", "")
    if(clean.isBlank()) return ""
    val parts = clean.split('.',limit = 2)
    val whole = parts[0].filter(Char::isDigit)
    val grouped = whole.reversed().chunked(3).joinToString(",").reversed()
    return grouped + if(parts.size == 2) "." + parts[1].filter(Char::isDigit) else ""

}

fun String.persianToEnglishDigits(): String {
    var result = this
    val persianDigits = listOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    val arabicDigits = listOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")

    for (i in 0..9) {
        result = result.replace(persianDigits[i], i.toString())
        result = result.replace(arabicDigits[i], i.toString())
    }
    return result
}

fun String.toPersianDigits(): String {
    val englishDigits = "0123456789"
    val persianDigits = "۰۱۲۳۴۵۶۷۸۹"

    return map { char ->
        val index = englishDigits.indexOf(char)
        if (index >= 0) persianDigits[index] else char
    }.joinToString("")
}

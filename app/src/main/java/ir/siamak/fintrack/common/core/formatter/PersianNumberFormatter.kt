package ir.siamak.fintrack.common.core.formatter

/** Normalizes Persian, Arabic, and Latin numeric input and renders Persian digits for UI. */
object PersianNumberFormatter {
    private const val LATIN_DIGITS = "0123456789"
    private const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"
    private const val ARABIC_DIGITS = "٠١٢٣٤٥٦٧٨٩"

    /** Converts a string containing Persian or Arabic numerals to ASCII numeric characters. */
    fun normalize(input: String): String = buildString(input.length) {
        input.forEach { character ->
            append(
                when {
                    character in PERSIAN_DIGITS -> LATIN_DIGITS[PERSIAN_DIGITS.indexOf(character)]
                    character in ARABIC_DIGITS -> LATIN_DIGITS[ARABIC_DIGITS.indexOf(character)]
                    character == '٬' -> ','
                    character == '٫' -> '.'
                    else -> character
                }
            )
        }
    }

    /** Renders all Latin digits in [input] using Persian digits. */
    fun toPersianDigits(input: String): String = buildString(input.length) {
        input.forEach { character ->
            append(when {
                character in LATIN_DIGITS -> PERSIAN_DIGITS[LATIN_DIGITS.indexOf(character)]
                character in ARABIC_DIGITS -> PERSIAN_DIGITS[ARABIC_DIGITS.indexOf(character)]
                else -> character
            })
        }
    }

    /** Formats an integer with Persian numerals while retaining a numeric source of truth. */
    fun format(value: Long): String = toPersianDigits(java.text.NumberFormat.getIntegerInstance(java.util.Locale.US).format(value))

    /** Group quantities, not dates, telephone/account numbers or document identifiers. */
    fun display(input: String): String = toPersianDigits(Regex("(?<![\\p{L}\\d/.:,٬-])[0-9۰-۹٠-٩]{4,}(?:[.٫][0-9۰-۹٠-٩]+)?(?![\\p{L}\\d/.:,٬-])").replace(input) {
        val raw = normalize(it.value)
        val whole = raw.substringBefore('.')
        if(whole.startsWith('0') && whole.length >= 8) it.value else whole.reversed().chunked(3).joinToString(",").reversed() + if('.' in raw) "." + raw.substringAfter('.') else ""
    })
}

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
        input.forEach { character -> append(if (character in LATIN_DIGITS) PERSIAN_DIGITS[LATIN_DIGITS.indexOf(character)] else character) }
    }

    /** Formats an integer with Persian numerals while retaining a numeric source of truth. */
    fun format(value: Long): String = toPersianDigits(value.toString())
}

package ir.siamak.fintrack.common.core.formatter

/** Formats integer monetary values for Persian UI without changing stored numeric values. */
object MoneyFormatter {
    const val DEFAULT_CURRENCY = "تومان"

    /** Returns a grouped Persian amount followed by its currency label. */
    fun format(amount: Long, currency: String = DEFAULT_CURRENCY): String {
        val grouped = "%,d".format(java.util.Locale.US, amount).replace(',', '٬')
        return "${PersianNumberFormatter.toPersianDigits(grouped)} $currency"
    }
}

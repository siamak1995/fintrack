package ir.siamak.fintrack.common.core.formatter

/** Formats percentage values for Persian UI. */
object PercentageFormatter {
    /** Returns a Persian number followed by the percent sign. */
    fun format(value: Int): String = "${PersianNumberFormatter.format(value.toLong())}٪"
}

package ir.siamak.fintrack.core.datepicker.model

/**
 * Immutable Persian (Jalali) calendar date.
 *
 * This model keeps only structural validation:
 * - year > 0
 * - month in 1..12
 * - day in 1..31
 *
 * Full calendar validity such as month length and leap-year rules
 * should be checked by calendar engine utilities.
 */
data class PersianDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<PersianDate> {

    init {
        require(year > 0) { "Year must be greater than 0." }
        require(month in 1..12) { "Month must be between 1 and 12." }
        require(day in 1..31) { "Day must be between 1 and 31." }
    }

    override fun compareTo(other: PersianDate): Int {
        return compareValuesBy(this, other, PersianDate::year, PersianDate::month, PersianDate::day)
    }

    fun isBefore(other: PersianDate): Boolean = this < other

    fun isAfter(other: PersianDate): Boolean = this > other

    fun isSameMonth(other: PersianDate): Boolean {
        return year == other.year && month == other.month
    }

    fun toCompactString(): String {
        return "%04d/%02d/%02d".format(year, month, day)
    }

    fun toPersianMonth(): PersianMonth = PersianMonth(year = year, month = month)
}

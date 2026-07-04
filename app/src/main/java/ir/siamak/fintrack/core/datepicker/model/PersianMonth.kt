package ir.siamak.fintrack.core.datepicker.model

/**
 * Immutable Persian (Jalali) year-month value.
 */
data class PersianMonth(
    val year: Int,
    val month: Int
) : Comparable<PersianMonth> {

    init {
        require(year > 0) { "Year must be greater than 0." }
        require(month in 1..12) { "Month must be between 1 and 12." }
    }

    override fun compareTo(other: PersianMonth): Int {
        return compareValuesBy(this, other, PersianMonth::year, PersianMonth::month)
    }

    fun atDay(day: Int): PersianDate = PersianDate(year = year, month = month, day = day)

    fun previous(): PersianMonth {
        return if (month == 1) PersianMonth(year - 1, 12) else PersianMonth(year, month - 1)
    }

    fun next(): PersianMonth {
        return if (month == 12) PersianMonth(year + 1, 1) else PersianMonth(year, month + 1)
    }

    fun toCompactString(): String {
        return "%04d/%02d".format(year, month)
    }
}

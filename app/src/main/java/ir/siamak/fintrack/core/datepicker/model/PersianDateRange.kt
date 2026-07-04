package ir.siamak.fintrack.core.datepicker.model

/**
 * Represents a Persian date range selection.
 *
 * A range may be:
 * - empty
 * - start-only
 * - complete
 */
data class PersianDateRange(
    val start: PersianDate? = null,
    val end: PersianDate? = null
) {

    val isEmpty: Boolean
        get() = start == null && end == null

    val isComplete: Boolean
        get() = start != null && end != null

    val isSingleDay: Boolean
        get() = isComplete && start == end

    fun contains(date: PersianDate): Boolean {
        if (start == null) return false
        if (end == null) return date == start
        return date >= start && date <= end
    }

    fun normalized(): PersianDateRange {
        if (start == null || end == null) return this
        return if (start <= end) this else copy(start = end, end = start)
    }

    fun withStart(date: PersianDate?): PersianDateRange {
        return copy(start = date).normalized()
    }

    fun withEnd(date: PersianDate?): PersianDateRange {
        return copy(end = date).normalized()
    }

    fun clear(): PersianDateRange = PersianDateRange()
}

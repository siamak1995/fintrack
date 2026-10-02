package ir.siamak.fintrack.common.core.datepicker.model

enum class PersianWeekDay(val index: Int) {
    SATURDAY(0),
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6);

    companion object {
        val ordered: List<PersianWeekDay> = entries.sortedBy(PersianWeekDay::index)

        fun fromIndex(index: Int): PersianWeekDay {
            return ordered.first { it.index == index }
        }
    }
}


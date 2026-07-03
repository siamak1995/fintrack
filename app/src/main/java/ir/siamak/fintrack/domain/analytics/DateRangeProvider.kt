package ir.siamak.fintrack.domain.analytics

import java.time.LocalDate
import java.time.ZoneId

/**
 * تولید بازه‌های زمانی.
 */
class DateRangeProvider {

    /**
     * شروع و پایان ماه جاری
     */
    fun currentMonth(): Pair<Long, Long> {

        val today = LocalDate.now()

        val start = today
            .withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val end = today
            .withDayOfMonth(today.lengthOfMonth())
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return start to end

    }

}
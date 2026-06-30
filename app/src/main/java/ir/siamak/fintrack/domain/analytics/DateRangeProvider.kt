package ir.siamak.fintrack.domain.analytics

import java.time.LocalDate
import java.time.ZoneId

/**
 * این کلاس مسئول تولید بازه‌های زمانی است.
 *
 * مثال:
 *
 * - ماه جاری
 * - هفته جاری
 * - سال جاری
 *
 * این بازه‌ها برای گزارشات و داشبورد استفاده می‌شوند.
 */
class DateRangeProvider {

    /**
     * بازه زمانی ماه جاری.
     */
    fun currentMonth(): Pair<Long, Long> {

        val now = LocalDate.now()

        val start = now.withDayOfMonth(1)
        val end = now.withDayOfMonth(now.lengthOfMonth())

        val startMillis = start
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endMillis = end
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return startMillis to endMillis
    }
}

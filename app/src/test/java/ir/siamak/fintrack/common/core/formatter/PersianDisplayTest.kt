package ir.siamak.fintrack.common.core.formatter

import ir.siamak.fintrack.common.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.common.core.datepicker.model.*
import org.junit.Assert.*
import org.junit.Test

class PersianDisplayTest {
    @Test fun latinAndArabicDigitsAreLocalizedWithoutChangingIdentifiers() {
        val source = "S-123 / ١٢٣ / ۱۲۳"
        assertEquals("S-۱۲۳ / ۱۲۳ / ۱۲۳",PersianNumberFormatter.toPersianDigits(source))
        assertEquals("S-123 / ١٢٣ / ۱۲۳",source)
    }
    @Test fun allCalendarLabelsUsePersianDigitsIndependentOfDeviceLocale() {
        val original = java.util.Locale.getDefault()
        try {
            java.util.Locale.setDefault(java.util.Locale.US)
            assertEquals("۱۴۰۵/۰۸/۰۲",PersianCalendarFormatter.formatDate(PersianDate(1405,8,2)))
            assertEquals("آبان ۱۴۰۵",PersianCalendarFormatter.formatMonthYear(PersianMonth(1405,8)))
            assertFalse(PersianCalendarFormatter.formatFullDate(PersianDate(1405,8,2)).any { it in '0'..'9' })
        } finally { java.util.Locale.setDefault(original) }
    }
    @Test fun groupsSignedAmountsAndPreservesDatesAndTrackingCodes() {
        assertEquals("مانده: -۱,۲۳۴,۵۶۷.۵ تومان", PersianNumberFormatter.display("مانده: -1234567.5 تومان"))
        assertEquals("+۱,۲۳۴", PersianNumberFormatter.display("+1234"))
        assertEquals("۱۴۰۵/۰۸/۰۴ · ۰۹۱۲۳۴۵۶۷۸۹ · S-۱۴۰۵۰۸۰۴-۰۰۰۱", PersianNumberFormatter.display("1405/08/04 · 09123456789 · S-14050804-0001"))
        assertEquals("۱,۲۳۴,۵۶۷.۸۹", PersianNumberFormatter.display("۱۲۳۴۵۶۷٫۸۹"))
    }
}

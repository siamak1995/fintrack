package ir.siamak.fintrack.common.core.formatter

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for the shared Persian presentation formatters. */
class PersianFormatterTest {
    @Test fun normalizesPersianArabicAndSeparators() = assertEquals("12,345.6", PersianNumberFormatter.normalize("۱۲٬٣٤٥٫۶"))
    @Test fun formatsMoneyWithPersianDigitsAndGrouping() = assertEquals("۱٬۲۵۰٬۰۰۰ تومان", MoneyFormatter.format(1_250_000))
    @Test fun formatsPercentage() = assertEquals("۱۲٪", PercentageFormatter.format(12))
    @Test fun formatsGregorianDateAsJalali() = assertEquals("۱۴۰۵/۰۷/۱۱", DateFormatter.formatJalali(LocalDate.of(2026, 10, 3)))
}

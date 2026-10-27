package ir.siamak.fintrack.common.core.formatter

import org.junit.Assert.*
import org.junit.Test

class PersianAmountWordsTest {
    @Test fun examplesAndGroupBoundaries() {
        assertEquals("دوازده هزار و پانصد",PersianAmountWords.format("12500"))
        assertEquals("یک میلیون و یک",PersianAmountWords.format("1000001"))
        assertEquals("صفر تومان",PersianAmountWords.format("0","تومان"))
        assertEquals("صد و یازده",PersianAmountWords.format("111"))
        assertEquals("نهصد و نود و نه",PersianAmountWords.format("999"))
    }
    @Test fun localizedDigitsAndFractionalRialConversion() {
        assertEquals("دوازده هزار و پانصد",PersianAmountWords.format("۱۲٬۵۰۰"))
        assertEquals("ده ممیز یک دهم تومان",PersianAmountWords.format("10.10","تومان"))
        assertEquals("منفی یک ممیز یک صدم",PersianAmountWords.format("-1.01"))
    }
    @Test fun typingAndClearingNeverCrashesOrRetainsOldText() {
        assertEquals("",PersianAmountWords.format(""))
        assertEquals("",PersianAmountWords.format("."))
        assertEquals("یک",PersianAmountWords.format("1."))
        assertEquals("دوازده",PersianAmountWords.format("12"))
        assertEquals("صد و بیست و پنج",PersianAmountWords.format("125"))
        assertEquals("",PersianAmountWords.format("NaN"))
        assertEquals("",PersianAmountWords.format("1e1000000"))
    }
    @org.junit.Test fun decimalAmountsKeepTheirFractionAndLargeInputsDoNotOverflowLong() {
        org.junit.Assert.assertEquals("12,500.75",ir.siamak.fintrack.common.core.extensions.formatAmount("۱۲۵۰۰٫۷۵"))
        org.junit.Assert.assertEquals("12,345,678,901,234,567,890",ir.siamak.fintrack.common.core.extensions.formatAmount("12345678901234567890"))
    }
}

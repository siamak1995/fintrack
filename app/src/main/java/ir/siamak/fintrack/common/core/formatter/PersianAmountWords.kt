package ir.siamak.fintrack.common.core.formatter

import java.math.BigInteger

object PersianAmountWords {
    private val ones = listOf("","یک","دو","سه","چهار","پنج","شش","هفت","هشت","نه","ده","یازده","دوازده","سیزده","چهارده","پانزده","شانزده","هفده","هجده","نوزده")
    private val tens = listOf("","","بیست","سی","چهل","پنجاه","شصت","هفتاد","هشتاد","نود")
    private val hundreds = listOf("","صد","دویست","سیصد","چهارصد","پانصد","ششصد","هفتصد","هشتصد","نهصد")
    private val scales = listOf("","هزار","میلیون","میلیارد","تریلیون","هزار تریلیون","میلیون تریلیون")
    private fun group(value: Int): String = listOf(hundreds[value / 100],
        if(value % 100 < 20) ones[value % 100] else listOf(tens[value % 100 / 10],ones[value % 10]).filter(String::isNotBlank).joinToString(" و "))
        .filter(String::isNotBlank).joinToString(" و ")
    private fun integer(value: String): String {
        val digits = value.trimStart('0').ifEmpty { "0" }
        if(digits == "0") return "صفر"
        return digits.reversed().chunked(3).map(String::reversed).mapIndexedNotNull { index,part ->
            val n = part.toInt()
            if(n == 0) null else (group(n) + " " + scales[index]).trim()
        }.asReversed().joinToString(" و ")
    }
    fun format(input: String, unit: String = ""): String {
        val raw = PersianNumberFormatter.normalize(input).replace(",", "").trim()
        if(raw.isEmpty() || raw.length > 40) return ""
        val number = raw.toBigDecimalOrNull()?.stripTrailingZeros() ?: return ""
        if(number.precision() > 27 || number.scale() !in -21..6) return ""
        val absolute = number.abs().toPlainString()
        val whole = absolute.substringBefore('.')
        if(whole.length > 21) return ""
        val fraction = absolute.substringAfter('.',"")
        val suffixes = listOf("","دهم","صدم","هزارم","ده‌هزارم","صدهزارم","میلیونیم")
        val text = (if(number.signum() < 0) "منفی " else "") + integer(whole) +
            (if(fraction.isNotEmpty() && fraction.toBigInteger() != BigInteger.ZERO) " ممیز " + integer(fraction) + " " + suffixes[fraction.length] else "")
        return text + if(unit.isBlank()) "" else " $unit"
    }
}

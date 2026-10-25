package ir.siamak.fintrack.common.core.formatter

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*

/** Formatting only; the editable value and financial parser remain ungrouped. */
object GroupedNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val point = raw.indexOfFirst { it == '.' || it == '٫' }.let { if(it < 0) raw.length else it }
        val positions = (1 until point).filter { raw[it].isDigit() && raw[it-1].isDigit() && (point-it)%3 == 0 }
        val rendered = buildString { raw.forEachIndexed { index,c -> if(index in positions) append(','); append(c) } }
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset + positions.count { it <= offset }
            override fun transformedToOriginal(offset: Int): Int {
                return (offset - positions.mapIndexed { index,position -> position+index }.count { it < offset }).coerceIn(0,raw.length)
            }
        }
        return TransformedText(AnnotatedString(PersianNumberFormatter.toPersianDigits(rendered)), mapping)
    }
}

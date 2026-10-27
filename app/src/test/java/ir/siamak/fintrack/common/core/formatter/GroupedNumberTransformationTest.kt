package ir.siamak.fintrack.common.core.formatter

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.*
import org.junit.Test

class GroupedNumberTransformationTest {
    @Test fun signedDecimalAndPersianInputsKeepCursorMappingWithinBounds() {
        listOf("", "0", "123", "1234", "1234567", "-1234567.89", "۱۲۳۴۵۶۷٫۸۹", "1234.").forEach { raw ->
            val transformed = GroupedNumberTransformation.filter(AnnotatedString(raw))
            assertEquals(PersianNumberFormatter.toPersianDigits(raw), transformed.text.text.replace(",", ""))
            val positions = (0..raw.length).map(transformed.offsetMapping::originalToTransformed)
            assertEquals(positions.sorted(), positions)
            positions.forEachIndexed { index, position ->
                assertTrue(position in 0..transformed.text.length)
                assertEquals(index, transformed.offsetMapping.transformedToOriginal(position))
            }
            (0..transformed.text.length).forEach { position ->
                assertTrue(transformed.offsetMapping.transformedToOriginal(position) in 0..raw.length)
            }
        }
        assertEquals("-۱,۲۳۴,۵۶۷.۸۹", GroupedNumberTransformation.filter(AnnotatedString("-1234567.89")).text.text)
    }
}

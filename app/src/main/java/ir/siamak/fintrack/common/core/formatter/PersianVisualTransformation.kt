package ir.siamak.fintrack.common.core.formatter

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/** Display only: never changes the stored value, cursor positions, or copy/paste input. */
object PersianVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString) = TransformedText(
        AnnotatedString(PersianNumberFormatter.toPersianDigits(text.text)), OffsetMapping.Identity
    )
}

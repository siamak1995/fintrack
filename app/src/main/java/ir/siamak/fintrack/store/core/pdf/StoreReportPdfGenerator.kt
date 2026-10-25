package ir.siamak.fintrack.store.core.pdf

import android.graphics.Typeface
import ir.siamak.fintrack.store.domain.model.StoreReport
import java.io.OutputStream

/** Full filtered report, with repeated table headers and the same Persian renderer as invoices. */
class StoreReportPdfGenerator {
    fun generate(report: StoreReport, output: OutputStream, regular: Typeface? = null, bold: Typeface? = null) {
        InvoicePdfGenerator().render(StoreReportPrintLayout().create(report),output,regular,bold)
    }
}

package ir.siamak.fintrack.store.core.pdf

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import ir.siamak.fintrack.store.core.formatter.InvoiceTrackingCode
import ir.siamak.fintrack.store.domain.model.Invoice
import java.io.OutputStream

/** A5 reference layout, using only live invoice data and the selected logo/signature. */
class InvoicePdfGenerator {
    fun generate(invoice: Invoice, output: OutputStream, logo: Bitmap? = null, signature: Bitmap? = null, related: List<Invoice>? = null,
                 regularTypeface: Typeface? = null, boldTypeface: Typeface? = null) {
        val layout = InvoicePrintLayout().create(invoice,related)
        val document = PdfDocument()
        val ink = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
        val border = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY; style = Paint.Style.STROKE; strokeWidth = .4f }
        val qr = TrackingQr.encode(InvoiceTrackingCode.of(invoice))
        try {
            layout.pages.forEachIndexed { index,elements ->
                val page = document.startPage(PdfDocument.PageInfo.Builder(layout.width,layout.height,index+1).create())
                val canvas = page.canvas
                canvas.drawColor(Color.WHITE)
                elements.forEach { element ->
                    val x = element.x; val y = element.y; val w = element.width; val h = element.height
                    when(element.kind) {
                        "line" -> canvas.drawLine(x,y,x+w,y,border)
                        "vertical" -> canvas.drawLine(x,y,x,y+h,border)
                        "box" -> canvas.drawRect(x,y,x+w,y+h,border)
                        "text" -> {
                            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                                textSize = element.size; color = Color.BLACK
                                typeface = (if(element.bold) boldTypeface else regularTypeface) ?: Typeface.create("sans-serif",if(element.bold) Typeface.BOLD else Typeface.NORMAL)
                            }
                            while(paint.measureText(element.text) > w && paint.textSize > 4f) paint.textSize -= .25f
                            val text = StaticLayout.Builder.obtain(element.text,0,element.text.length,paint,w.toInt().coerceAtLeast(1))
                                .setTextDirection(TextDirectionHeuristics.RTL).setIncludePad(false)
                                .setAlignment(when(element.align) { "center" -> Layout.Alignment.ALIGN_CENTER; "left" -> Layout.Alignment.ALIGN_OPPOSITE; else -> Layout.Alignment.ALIGN_NORMAL }).build()
                            canvas.save(); canvas.translate(x,y); text.draw(canvas); canvas.restore()
                        }
                        "logo", "signature" -> {
                            val bitmap = if(element.kind == "logo") logo else signature
                            bitmap?.let {
                                val scale = minOf(w/it.width,h/it.height)
                                val bw = it.width*scale; val bh = it.height*scale
                                canvas.drawBitmap(it,null,RectF(x+(w-bw)/2,y+(h-bh)/2,x+(w+bw)/2,y+(h+bh)/2),ink)
                            }
                        }
                        "qr" -> {
                            val cell = w/(qr.size+8)
                            qr.forEachIndexed { row,values -> values.forEachIndexed { col,dark ->
                                if(dark) canvas.drawRect(x+(col+4)*cell,y+(row+4)*cell,x+(col+5)*cell,y+(row+5)*cell,ink)
                            } }
                        }
                    }
                }
                document.finishPage(page)
            }
            document.writeTo(output)
        } finally { document.close() }
    }
}

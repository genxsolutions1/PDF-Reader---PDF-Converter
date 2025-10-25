package com.genxsolutions.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min

object PdfConverter {

    /**
     * Creates a single PDF file that contains all given images, one per page.
     * Pages use A4 size in points (595 x 842). Images are scaled to fit with small margins.
     * Returns the output File if successful, else null.
     */
    fun createPdfFromImages(
        context: Context,
        imageFiles: List<File>,
        outputName: String = defaultOutputName()
    ): File? {
        if (imageFiles.isEmpty()) return null
        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 24f

        try {
            imageFiles.forEachIndexed { index, file ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(file.absolutePath, options)
                val sampleSize = calculateInSampleSize(options, 2000, 2000)
                val decodeOpts = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                val src = BitmapFactory.decodeFile(file.absolutePath, decodeOpts) ?: return@forEachIndexed

                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
                val page = pdf.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                val contentW = pageWidth - margin * 2
                val contentH = pageHeight - margin * 2

                // Compute scale to fit while preserving aspect
                val scale = min(contentW / src.width.toFloat(), contentH / src.height.toFloat())
                val destW = src.width * scale
                val destH = src.height * scale

                val left = (pageWidth - destW) / 2f
                val top = (pageHeight - destH) / 2f

                val matrix = Matrix()
                matrix.postScale(scale, scale)
                matrix.postTranslate(left, top)

                canvas.drawBitmap(src, matrix, null)
                pdf.finishPage(page)
                src.recycle()
            }

            val outDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: File(context.filesDir, "exports")
            if (!outDir.exists()) outDir.mkdirs()
            val outFile = File(outDir, "$outputName.pdf")
            FileOutputStream(outFile).use { fos ->
                pdf.writeTo(fos)
            }
            return outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            pdf.close()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (width: Int, height: Int) = options.outWidth to options.outHeight
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun defaultOutputName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return "PDF_${sdf.format(System.currentTimeMillis())}"
    }
}

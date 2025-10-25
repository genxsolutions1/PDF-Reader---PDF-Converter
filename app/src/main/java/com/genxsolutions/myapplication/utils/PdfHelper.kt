package com.genxsolutions.myapplication.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream

object PdfHelper {
    
    fun savePdfToDownloads(context: Context, pdfFile: File): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFile.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it).use { outputStream ->
                        FileInputStream(pdfFile).use { inputStream ->
                            inputStream.copyTo(outputStream!!)
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDir, pdfFile.name)
                pdfFile.copyTo(destFile, overwrite = true)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sharePdf(context: Context, pdfFile: File) {
        try {
            Log.d("PdfHelper", "Sharing PDF: ${pdfFile.absolutePath}")
            Log.d("PdfHelper", "File exists: ${pdfFile.exists()}")
            Log.d("PdfHelper", "File can read: ${pdfFile.canRead()}")
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            Log.d("PdfHelper", "FileProvider URI: $uri")
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            Log.d("PdfHelper", "Share intent launched successfully")
        } catch (e: Exception) {
            Log.e("PdfHelper", "Error sharing PDF", e)
            e.printStackTrace()
            throw e
        }
    }
}

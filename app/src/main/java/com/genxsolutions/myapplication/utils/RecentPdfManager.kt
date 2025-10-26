package com.genxsolutions.myapplication.utils

import android.content.Context
import android.os.Environment
import java.io.File

object RecentPdfManager {
    
    /**
     * Get list of recent PDF files (max 10), sorted by last modified date
     */
    fun getRecentPdfs(context: Context): List<File> {
        val pdfDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: File(context.filesDir, "exports")
        
        if (!pdfDir.exists()) {
            return emptyList()
        }
        
        return pdfDir.listFiles { file ->
            file.isFile && file.extension.equals("pdf", ignoreCase = true)
        }?.sortedByDescending { it.lastModified() }
            ?.take(10)
            ?: emptyList()
    }
    
    /**
     * Format file size to human readable format
     */
    fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> String.format("%.1f KB", sizeInBytes / 1024.0)
            else -> String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0))
        }
    }
}

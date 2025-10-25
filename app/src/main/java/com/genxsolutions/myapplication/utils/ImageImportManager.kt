package com.genxsolutions.myapplication.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object ImageImportManager {
    private const val IMPORT_DIR = "imported_images"

    data class ImportResult(
        val savedFiles: List<File>,
        val failures: List<Uri>
    )

    fun importImages(context: Context, uris: List<Uri>): ImportResult {
        if (uris.isEmpty()) return ImportResult(emptyList(), emptyList())

        val imagesDir = File(context.filesDir, IMPORT_DIR).apply { mkdirs() }
        val saved = mutableListOf<File>()
        val failed = mutableListOf<Uri>()

        for (uri in uris) {
            try {
                val name = resolveDisplayName(context.contentResolver, uri)
                val dest = uniqueFile(imagesDir, name)
                context.contentResolver.openInputStream(uri).use { input ->
                    if (input == null) {
                        failed += uri
                    } else {
                        copyToFile(input, dest)
                        saved += dest
                    }
                }
            } catch (t: Throwable) {
                failed += uri
            }
        }
        return ImportResult(saved, failed)
    }

    private fun resolveDisplayName(resolver: ContentResolver, uri: Uri): String {
        var name: String? = null
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        var cursor: Cursor? = null
        try {
            cursor = resolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = cursor.getString(idx)
            }
        } catch (_: Throwable) {
        } finally {
            cursor?.close()
        }
        if (name.isNullOrBlank()) {
            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            name = "IMG_$ts.jpg"
        }
        return name!!
    }

    private fun uniqueFile(dir: File, baseName: String): File {
        val dot = baseName.lastIndexOf('.')
        val stem = if (dot != -1) baseName.substring(0, dot) else baseName
        val ext = if (dot != -1) baseName.substring(dot) else ""

        // Suffix with millis + short UUID to be practically collision-free without IO loops
        val shortUuid = UUID.randomUUID().toString().substring(0, 8)
        val suffixed = "${stem}_$shortUuid${ext}"
        return File(dir, suffixed)
    }

    private fun copyToFile(input: InputStream, dest: File) {
        FileOutputStream(dest).use { out ->
            val buf = ByteArray(8 * 1024)
            while (true) {
                val read = input.read(buf)
                if (read <= 0) break
                out.write(buf, 0, read)
            }
            out.flush()
        }
    }
}

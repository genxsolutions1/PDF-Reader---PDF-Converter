package com.genxsolutions.myapplication.ui.screens.preview.filters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream

/**
 * Handles image cropping functionality using UCrop library.
 * Provides methods to launch crop UI and save cropped results.
 */
object CropImageHandler {

    /**
     * Launches the UCrop activity for the given image file.
     *
     * @param context Android context
     * @param file The image file to crop
     * @param launcher Activity result launcher for handling the crop result
     */
    fun launchCrop(
        context: Context,
        file: File,
        launcher: ActivityResultLauncher<Intent>
    ) {
        try {
            val inputUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val outputFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            val outputUri = Uri.fromFile(outputFile)

            val uCrop = UCrop.of(inputUri, outputUri)
                .withOptions(UCrop.Options().apply {
                    setCompressionQuality(90)
                    setFreeStyleCropEnabled(true)
                    setShowCropGrid(true)
                    setShowCropFrame(true)
                })

            launcher.launch(uCrop.getIntent(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Handles the crop result and saves the cropped image to the original file.
     *
     * @param context Android context
     * @param resultCode The result code from the crop activity
     * @param data The intent data containing the crop result
     * @param targetFile The file to save the cropped image to
     * @return True if the crop was successful and saved, false otherwise
     */
    fun handleCropResult(
        context: Context,
        resultCode: Int,
        data: Intent?,
        targetFile: File
    ): Boolean {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val outputUri = UCrop.getOutput(data)
            if (outputUri != null) {
                return saveCroppedImage(context, outputUri, targetFile)
            }
        }
        return false
    }

    /**
     * Saves the cropped image from the output URI to the target file.
     *
     * @param context Android context
     * @param outputUri The URI of the cropped image
     * @param targetFile The file to save the cropped image to
     * @return True if save was successful, false otherwise
     */
    private fun saveCroppedImage(
        context: Context,
        outputUri: Uri,
        targetFile: File
    ): Boolean {
        return try {
            context.contentResolver.openInputStream(outputUri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

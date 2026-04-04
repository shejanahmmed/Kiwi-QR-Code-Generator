package com.shejan.kiwi.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Utility object for handling file-related operations such as saving to gallery and sharing images.
 * Manages compatibility between different Android versions (Scoped Storage vs Legacy).
 */
object FileHelper {
    
    /**
     * Saves a [Bitmap] to the device's public gallery.
     * Uses [MediaStore] for modern Android versions (Q+) to ensure scoped storage compliance.
     * 
     * @param context The application context.
     * @param bitmap The QR code bitmap to save.
     * @param fileName The desired name for the image file.
     * @return True if the image was successfully saved, false otherwise.
     */
    fun saveToGallery(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        val resolver = context.contentResolver
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Kiwi")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollection, contentValues) ?: return false

        return try {
            val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            resolver.delete(imageUri, null, null)
            false
        }
    }

    /**
     * Shares a [Bitmap] with other applications using a [FileProvider].
     * Temporarily saves the image to the app's cache directory before sharing.
     * 
     * @param context The application context.
     * @param bitmap The QR code bitmap to share.
     * @param fileName The desired name for the temporary file.
     */
    fun shareImage(context: Context, bitmap: Bitmap, fileName: String) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "$fileName.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // Generate content URI using FileProvider for secure sharing
            val contentUri: Uri = FileProvider.getUriForFile(context, "com.shejan.kiwi.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, "image/png")
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(intent, "Share QR Code"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

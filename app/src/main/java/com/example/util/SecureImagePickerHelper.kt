package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

object SecureImagePickerHelper {
    private const val MAX_IMAGE_DIMENSION = 1080
    private const val JPEG_QUALITY = 85
    private const val DIRECTORY_NAME = "user_photos"

    /**
     * Resizes and saves a camera [Bitmap] securely to the app's internal private storage.
     * Returns the local file path (e.g. /data/user/0/.../user_photos/photo_123.jpg)
     */
    fun saveCameraBitmap(context: Context, bitmap: Bitmap): String? {
        return try {
            val resized = resizeBitmap(bitmap, MAX_IMAGE_DIMENSION)
            val dir = File(context.filesDir, DIRECTORY_NAME).apply { mkdirs() }
            val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { out ->
                resized.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                out.flush()
            }
            if (resized != bitmap) {
                resized.recycle()
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Reads a gallery [Uri], resizes with sampling to avoid OOM, and saves securely
     * to the app's internal private storage.
     */
    fun saveGalleryUri(context: Context, uri: Uri): String? {
        return try {
            // First decode bounds
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            val maxDim = max(options.outWidth, options.outHeight)
            var sampleSize = 1
            while (maxDim / (sampleSize * 2) >= MAX_IMAGE_DIMENSION) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val sampledBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return null

            val finalBitmap = resizeBitmap(sampledBitmap, MAX_IMAGE_DIMENSION)
            val dir = File(context.filesDir, DIRECTORY_NAME).apply { mkdirs() }
            val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { out ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                out.flush()
            }

            if (finalBitmap != sampledBitmap) {
                finalBitmap.recycle()
            }
            sampledBitmap.recycle()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resizes bitmap proportionally so neither width nor height exceeds [maxDimension].
     */
    private fun resizeBitmap(source: Bitmap, maxDimension: Int): Bitmap {
        val width = source.width
        val height = source.height
        if (width <= maxDimension && height <= maxDimension) {
            return source
        }
        val ratio = maxDimension.toFloat() / max(width, height)
        val targetWidth = (width * ratio).toInt()
        val targetHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }

    /**
     * Deletes a local photo file securely when removed by the user.
     */
    fun deletePhotoFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists() && file.isFile) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

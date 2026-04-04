package com.shejan.kiwi.logic

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

/**
 * Utility object for generating QR code bitmaps.
 * Uses the ZXing library for encoding text into a bit matrix.
 */
object QrGenerator {
    /**
     * Generates a QR code bitmap from the given text.
     * 
     * @param text The string to encode into the QR code.
     * @param size The width and height of the resulting bitmap in pixels.
     * @return A [Bitmap] of the QR code, or null if generation fails.
     */
    fun generate(text: String, size: Int = 512): Bitmap? {
        if (text.isEmpty()) return null
        
        return try {
            val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            // Convert bitMatrix to Bitmap
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

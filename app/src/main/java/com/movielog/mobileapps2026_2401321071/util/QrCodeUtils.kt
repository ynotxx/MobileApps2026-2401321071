package com.movielog.mobileapps2026_2401321071.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrCodeUtils {
    fun generateQrCodeBitmap(content: String, widthPx: Int = 512, heightPx: Int = 512): Bitmap? {
        if (content.isEmpty()) return null
        return try {
            val bitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                widthPx,
                heightPx
            )
            BarcodeEncoder().createBitmap(bitMatrix)
        } catch (e: Exception) {
            null
        }
    }
}

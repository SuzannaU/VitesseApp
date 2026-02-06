package com.openclassrooms.vitesseapp.ui.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory

interface BitmapDecoder {
    fun decode(bytes: ByteArray): Bitmap?
}

class AndroidBitmapDecoder : BitmapDecoder {
    override fun decode(bytes: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
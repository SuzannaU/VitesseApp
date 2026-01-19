package com.openclassrooms.vitesseapp.data.storage

import android.net.Uri

interface ImageStorage {
    suspend fun saveImage(uri: Uri): String
}
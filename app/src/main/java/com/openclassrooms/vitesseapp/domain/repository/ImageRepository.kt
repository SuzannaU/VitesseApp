package com.openclassrooms.vitesseapp.domain.repository

import android.net.Uri

interface ImageRepository {
    suspend fun saveImage(uri: Uri): String
}
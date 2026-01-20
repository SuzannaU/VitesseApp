package com.openclassrooms.vitesseapp.domain.usecase

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.repository.ImageRepository

class SaveImageUseCase(
    private val imageRepository: ImageRepository
) {
    suspend fun execute(uri: Uri): String {
        return imageRepository.saveImage(uri)
    }
}
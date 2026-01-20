package com.openclassrooms.vitesseapp.data.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import com.openclassrooms.vitesseapp.domain.repository.ImageRepository
import okio.IOException
import okio.use
import java.io.File
import java.io.FileOutputStream

class InternalImageStorage(private val context: Context) : ImageRepository {

    // TODO : if enough time, make the whole class testable

    override suspend fun saveImage(uri: Uri): String {

        lateinit var file: File

        try {
            file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Failed to openInputStream")
            val outputStream = FileOutputStream(file)

            inputStream.use { inputStream ->
                outputStream.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

        } catch (e: IOException) {
            Log.e("SaveImageToInternalStorageUseCase", "Error while saving image : ${e.message}")

        }

        val photoPath = file.absolutePath
        return photoPath
    }
}
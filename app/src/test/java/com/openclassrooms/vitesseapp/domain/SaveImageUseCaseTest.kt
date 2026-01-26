package com.openclassrooms.vitesseapp.domain

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.repository.ImageRepository
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SaveImageUseCaseTest {

    val imageRepository = mockk<ImageRepository>()
    val saveImageUseCase = SaveImageUseCase(imageRepository)

    @Test
    fun execute_shouldSendUriToRepository() = runTest {

        val uri = mockk<Uri>()
        coEvery { imageRepository.saveImage(any()) } returns "path"

        val result = saveImageUseCase.execute(uri)

        assertEquals("path", result)
        coVerify { imageRepository.saveImage(any()) }
    }
}
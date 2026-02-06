package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.UpdateFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UpdateFavoriteUseCaseTest {

    private val candidateRepository = mockk<CandidateRepository>()
    private val updateFavoriteUseCase = UpdateFavoriteUseCase(candidateRepository)

    @Test
    fun execute_shouldSendDataToRepository() = runTest {

        val id = 1L
        val isFavorite = true
        val idCapture = slot<Long>()
        val favoriteCapture = slot<Boolean>()
        coEvery { candidateRepository.updateCandidateIsFavorite(capture(idCapture), capture(favoriteCapture)) } returns Unit

        updateFavoriteUseCase.execute(id, isFavorite)

        assertEquals(id, idCapture.captured)
        assertEquals(isFavorite, favoriteCapture.captured)
        coVerify { updateFavoriteUseCase.execute(any(), any()) }
    }
}
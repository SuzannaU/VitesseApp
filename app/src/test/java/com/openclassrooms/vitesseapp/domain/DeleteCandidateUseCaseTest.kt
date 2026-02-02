package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.DeleteCandidateUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeleteCandidateUseCaseTest {

    val candidateRepository = mockk<CandidateRepository>()
    val deleteCandidateUseCase = DeleteCandidateUseCase(candidateRepository)

    @Test
    fun execute_shouldCallRepository() = runTest {
        val id = 1L
        val idCapture = slot<Long>()
        coEvery { candidateRepository.deleteCandidate(capture(idCapture)) } returns Unit

        deleteCandidateUseCase.execute(id)

        assertEquals(id, idCapture.captured)
        coVerify { candidateRepository.deleteCandidate(any()) }
    }
}
package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SaveCandidateUseCaseTest {

    private val candidateRepository : CandidateRepository = mockk()
    private val saveCandidateUseCase = SaveCandidateUseCase(candidateRepository)

    @Test
    fun execute_shouldSendCandidateToRepository() = runTest {

        val candidateDto = CandidateDto(
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = ByteArray(1),
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryCentsInEur = 1,
            age = null,
        )

        val candidateDtoCapture = slot<CandidateDto>()
        coEvery { candidateRepository.saveCandidate(capture(candidateDtoCapture)) } returns Unit

        saveCandidateUseCase.execute(candidateDto)

        assertEquals(candidateDto, candidateDtoCapture.captured)
        coVerify { candidateRepository.saveCandidate(any()) }
    }
}
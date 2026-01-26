package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.domain.model.Candidate
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

    val candidateRepository : CandidateRepository = mockk()
    val saveCandidateUseCase = SaveCandidateUseCase(candidateRepository)

    @Test
    fun execute_shouldSendCandidateDtoToRepository() = runTest {

        val candidate = Candidate(
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryInEur = 1,
            age = null,
        )

        val expectedCandidateDto = CandidateDto(
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryInEur = 1,
        )

        val candidateDtoCapture = slot<CandidateDto>()

        coEvery { candidateRepository.saveCandidate(capture(candidateDtoCapture)) } returns Unit

        saveCandidateUseCase.execute(candidate)

        assertEquals(expectedCandidateDto, candidateDtoCapture.captured)
        coVerify { candidateRepository.saveCandidate(any()) }
    }
}
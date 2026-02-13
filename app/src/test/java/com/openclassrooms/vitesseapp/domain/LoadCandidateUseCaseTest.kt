package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoadCandidateUseCaseTest {

    private val candidateRepository = mockk<CandidateRepository>()
    private val loadCandidateUseCase = LoadCandidateUseCase(candidateRepository)
    private lateinit var candidateDto: CandidateDto

    @Test
    fun execute_shouldCallRepositoryAndReturnCandidate() = runTest {
        val candidateId = 1L
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)
        val bytes = ByteArray(1)

        candidateDto = CandidateDto(
            candidateId = candidateId,
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = bytes,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            notes = null,
            age = expectedAge,
            salaryCentsInEur = 1,
        )
        val idCapture = slot<Long>()
        coEvery { candidateRepository.fetchCandidate(capture(idCapture)) } returns candidateDto

        val result = loadCandidateUseCase.execute(candidateId)
        
        assertEquals(candidateId, idCapture.captured)
        assertEquals(candidateDto, result)
        coVerify { candidateRepository.fetchCandidate(any()) }
    }
}
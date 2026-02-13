package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoadAllCandidatesUseCaseTest {

    private val candidateRepository: CandidateRepository = mockk()
    private val loadAllCandidatesUseCase = LoadAllCandidatesUseCase(candidateRepository)
    private lateinit var candidateDtos: List<CandidateDto>

    @Test
    fun execute_shouldReturnFlowOfCandidates() = runTest {
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)
        val bytes = ByteArray(1)

        candidateDtos = listOf(
            CandidateDto(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                age = expectedAge,
                salaryCentsInEur = 1,
            ),
            CandidateDto(
                candidateId = 2,
                firstname = "firstname",
                lastname = "lastname",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                age = expectedAge,
                notes = null,
                salaryCentsInEur = 1,
            ),
        )

        every { candidateRepository.fetchAllCandidates() } returns flowOf(candidateDtos)

        val result = loadAllCandidatesUseCase.execute().first()

        assertEquals(candidateDtos, result)
        verify { candidateRepository.fetchAllCandidates() }
    }
}